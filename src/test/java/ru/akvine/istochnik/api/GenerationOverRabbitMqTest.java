package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.config.rabbitmq.RabbitMqGenerationProperties;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.listeners.GenerationRequestListener;

// Тесту нужен настоящий брокер, поэтому без Docker он пропускается
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = "rabbitmq.enabled=true")
@DisplayName("Generation over RabbitMQ tests")
public class GenerationOverRabbitMqTest extends ApiBaseTest {
    private static final long REPLY_TIMEOUT_MILLIS = 30_000;

    @Container
    private static final RabbitMQContainer RABBIT_MQ = new RabbitMQContainer("rabbitmq:4.1-alpine");

    @DynamicPropertySource
    static void rabbitMqProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBIT_MQ::getHost);
        registry.add("spring.rabbitmq.port", RABBIT_MQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT_MQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT_MQ::getAdminPassword);
    }

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private RabbitMqGenerationProperties rabbitMqGenerationProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void createClient() {
        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setReplyTimeout(REPLY_TIMEOUT_MILLIS);
    }

    @Test
    @DisplayName("Request with reply-to - file is returned to the sender")
    void successful_generate_with_reply_to() throws IOException {
        Message reply = rabbitTemplate.sendAndReceive(
                "", rabbitMqGenerationProperties.getRequestQueue(), message(request(5), null));

        assertThat(reply).isNotNull();
        assertThat(header(reply, GenerationRequestListener.STATUS_HEADER)).isEqualTo("SUCCESS");
        assertThat(header(reply, GenerationRequestListener.FILE_NAME_HEADER)).isEqualTo("response.csv");
        assertThat(reply.getMessageProperties().getContentType()).isEqualTo("application/csv");
        // Заголовок и пять строк
        assertThat(new String(reply.getBody(), StandardCharsets.UTF_8))
                .startsWith("id")
                .hasLineCount(6);
    }

    @Test
    @DisplayName("Request without reply-to - file is sent to response queue with correlation id of the request")
    void successful_generate_without_reply_to() throws IOException {
        rabbitTemplate.send("", rabbitMqGenerationProperties.getRequestQueue(), message(request(2), "request-42"));

        Message reply = rabbitTemplate.receive(rabbitMqGenerationProperties.getResponseQueue(), REPLY_TIMEOUT_MILLIS);

        assertThat(reply).isNotNull();
        assertThat(reply.getMessageProperties().getCorrelationId()).isEqualTo("request-42");
        assertThat(header(reply, GenerationRequestListener.STATUS_HEADER)).isEqualTo("SUCCESS");
        assertThat(new String(reply.getBody(), StandardCharsets.UTF_8)).hasLineCount(3);
    }

    @Test
    @DisplayName("Invalid request - error is returned to the sender, message is not redelivered")
    void fail_generate_with_invalid_request() throws IOException {
        Message reply = rabbitTemplate.sendAndReceive(
                "", rabbitMqGenerationProperties.getRequestQueue(), message(request(-1), null));

        assertThat(reply).isNotNull();
        assertThat(header(reply, GenerationRequestListener.STATUS_HEADER)).isEqualTo("FAIL");
        assertThat(objectMapper.readTree(reply.getBody()).get("code").asText())
                .isEqualTo("request.fields.invalid.error");
        // Запрос подтверждён, а не возвращён в очередь
        assertThat(rabbitTemplate.receive(rabbitMqGenerationProperties.getRequestQueue()))
                .isNull();
    }

    private String header(Message message, String name) {
        return message.getMessageProperties().getHeader(name);
    }

    private Message message(GenerateTableRequest request, String correlationId) throws IOException {
        return MessageBuilder.withBody(objectMapper.writeValueAsBytes(request))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(correlationId)
                .build();
    }

    private GenerateTableRequest request(int size) {
        ColumnDto column = new ColumnDto()
                .setName("id")
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());

        return new GenerateTableRequest()
                .setSize(size)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column));
    }
}
