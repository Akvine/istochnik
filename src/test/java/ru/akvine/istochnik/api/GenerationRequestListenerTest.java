package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.listeners.GenerationRequestListener;

// Брокера в тесте нет: слушатель создаётся, но к очереди не подключается, а его метод вызывается напрямую
@TestPropertySource(
        properties = {
            "rabbitmq.enabled=true",
            "spring.rabbitmq.listener.simple.auto-startup=false",
            "rabbitmq.generation.request-queue=test.generation.requests",
            "rabbitmq.generation.response-queue=test.generation.responses",
            "rabbitmq.generation.max-file-size-kb=1"
        })
@DisplayName("Generation requests from RabbitMQ queue tests")
public class GenerationRequestListenerTest extends ApiBaseTest {

    @Autowired
    private GenerationRequestListener generationRequestListener;

    @Autowired
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Autowired
    private List<Queue> queues;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Listener reads request queue from settings, both queues are declared")
    void listener_reads_request_queue_from_settings() {
        assertThat(rabbitListenerEndpointRegistry.getListenerContainers())
                .singleElement()
                .isInstanceOfSatisfying(
                        AbstractMessageListenerContainer.class,
                        container -> assertThat(container.getQueueNames()).containsExactly("test.generation.requests"));
        assertThat(queues)
                .extracting(Queue::getName)
                .containsExactlyInAnyOrder("test.generation.requests", "test.generation.responses");
    }

    @Test
    @DisplayName("Valid request - reply contains generated file")
    void successful_generate_file() {
        GenerateTableRequest request = request(3, FileType.SQL, uuidColumn()).setTableName("users");

        Message reply = generationRequestListener.generate(message(request));

        assertThat(header(reply, GenerationRequestListener.STATUS_HEADER)).isEqualTo("SUCCESS");
        assertThat(header(reply, GenerationRequestListener.FILE_NAME_HEADER)).isEqualTo("users.sql");
        assertThat(reply.getMessageProperties().getContentType()).isEqualTo("application/sql");
        assertThat(new String(reply.getBody(), StandardCharsets.UTF_8))
                .startsWith("INSERT INTO users (ID) VALUES")
                .hasLineCount(4);
    }

    @Test
    @DisplayName("Request without table name - default file name is used")
    void successful_generate_file_with_default_name() {
        Message reply = generationRequestListener.generate(message(request(2, FileType.CSV, uuidColumn())));

        assertThat(header(reply, GenerationRequestListener.STATUS_HEADER)).isEqualTo("SUCCESS");
        assertThat(header(reply, GenerationRequestListener.FILE_NAME_HEADER)).isEqualTo("response.csv");
        assertThat(reply.getMessageProperties().getContentType()).isEqualTo("application/csv");
        assertThat(new String(reply.getBody(), StandardCharsets.UTF_8)).startsWith("id");
    }

    @Test
    @DisplayName("Request with invalid fields - reply contains errors by columns")
    void fail_generate_with_invalid_fields() throws IOException {
        ColumnDto column = uuidColumn().setGenerationStrategy("unknown");

        Message reply = generationRequestListener.generate(message(request(-1, FileType.CSV, column)));

        JsonNode error = error(reply);
        assertThat(error.get("status").asText()).isEqualTo("FAIL");
        assertThat(error.get("code").asText()).isEqualTo("request.fields.invalid.error");
        assertThat(error.get("generalInfoErrors").asText()).contains("size can't be less than 0");
        assertThat(error.get("columnErrors").has("id")).isTrue();
    }

    @Test
    @DisplayName("Message body is not a generation request - reply contains error")
    void fail_generate_with_invalid_body() throws IOException {
        Message notJson = MessageBuilder.withBody("size = 3".getBytes(StandardCharsets.UTF_8))
                .build();
        Message nullRequest =
                MessageBuilder.withBody("null".getBytes(StandardCharsets.UTF_8)).build();
        Message emptyBody = MessageBuilder.withBody(new byte[0]).build();

        for (Message requestMessage : List.of(notJson, nullRequest, emptyBody)) {
            JsonNode error = error(generationRequestListener.generate(requestMessage));

            assertThat(error.get("code").asText()).isEqualTo("request.body.invalid.error");
            assertThat(error.get("message").asText()).startsWith("Message body is not a valid generation request");
        }
    }

    @Test
    @DisplayName("Error while generating column - reply contains generation error")
    void fail_generate_with_generation_error() throws IOException {
        // Два уникальных значения из диапазона с одним числом: ошибка обнаруживается только при генерации
        ColumnDto column = new ColumnDto()
                .setName("code")
                .setType(BaseType.INTEGER.getValue())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto()
                        .setRangeType(RangeType.RANDOM.getType())
                        .setStart("1")
                        .setEnd("2")
                        .setUnique(true)
                        .setNotNull(true));

        JsonNode error = error(generationRequestListener.generate(message(request(2, FileType.CSV, column))));

        assertThat(error.get("code").asText()).isEqualTo("generation.error");
        assertThat(error.get("message").asText()).contains("Max attempts to generation");
    }

    @Test
    @DisplayName("Unexpected error - reply contains error instead of redelivery of the message")
    void fail_generate_with_unexpected_error() throws IOException {
        // Колонка без config приводит к ошибке, которую проверка запроса не находит
        ColumnDto column = uuidColumn().setConfig(null);

        JsonNode error = error(generationRequestListener.generate(message(request(2, FileType.CSV, column))));

        assertThat(error.get("code").asText()).isEqualTo("generation.error");
        assertThat(error.get("message").asText()).isEqualTo("Unexpected error while handling generation request");
    }

    @Test
    @DisplayName("Generated file is greater than limit - reply contains error")
    void fail_generate_when_file_is_too_large() throws IOException {
        // 100 UUID занимают больше лимита в 1 килобайт
        JsonNode error = error(generationRequestListener.generate(message(request(100, FileType.CSV, uuidColumn()))));

        assertThat(error.get("code").asText()).isEqualTo("file.size.exceeded.error");
        assertThat(error.get("message").asText()).contains("is greater than max = [1024] bytes");
    }

    private JsonNode error(Message reply) throws IOException {
        assertThat(header(reply, GenerationRequestListener.STATUS_HEADER)).isEqualTo("FAIL");
        assertThat(reply.getMessageProperties().getContentType()).isEqualTo(MessageProperties.CONTENT_TYPE_JSON);
        return objectMapper.readTree(reply.getBody());
    }

    private String header(Message message, String name) {
        return message.getMessageProperties().getHeader(name);
    }

    private Message message(GenerateTableRequest request) {
        try {
            return MessageBuilder.withBody(objectMapper.writeValueAsBytes(request))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private GenerateTableRequest request(int size, FileType fileType, ColumnDto column) {
        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(fileType.name())
                .setColumns(List.of(column));
    }

    private ColumnDto uuidColumn() {
        return new ColumnDto()
                .setName("id")
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());
    }
}
