package ru.akvine.istochnik.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.ErrorResponse;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.config.rabbitmq.RabbitMqGenerationProperties;
import ru.akvine.istochnik.constants.ErrorConstants;
import ru.akvine.istochnik.controllers.converters.GeneratorConverter;
import ru.akvine.istochnik.controllers.dto.GenerateDataErrorResponse;
import ru.akvine.istochnik.controllers.validators.GeneratorValidator;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.ResponseStatus;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.exceptions.validation.ConfigValidationException;
import ru.akvine.istochnik.services.FileTableService;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

/**
 * Принимает запросы на генерацию из очереди RabbitMQ. Тело сообщения - тот же JSON, что и у POST /v1/generator.
 * Ответ отправляется по адресу reply-to запроса, а если его нет - в очередь ответов из настроек.
 * Ошибка запроса возвращается ответным сообщением, а не исключением: повторная доставка её не исправит,
 * и сообщение вернулось бы в очередь навсегда
 */
@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class GenerationRequestListener {
    public static final String STATUS_HEADER = "status";
    public static final String FILE_NAME_HEADER = "fileName";

    private static final String DEFAULT_FILE_NAME = "response";
    private static final long BYTES_IN_KILOBYTE = 1024;

    private final ObjectMapper objectMapper;
    private final GeneratorValidator generatorValidator;
    private final GeneratorConverter generatorConverter;
    private final GeneratorFacade generatorFacade;
    private final FileTableService fileTableService;
    private final RabbitMqGenerationProperties rabbitMqGenerationProperties;

    @RabbitListener(queues = "#{rabbitMqGenerationProperties.requestQueue}")
    @SendTo("#{rabbitMqGenerationProperties.responseQueue}")
    public Message generate(Message requestMessage) {
        try {
            GenerateTableRequest request = objectMapper.readValue(requestMessage.getBody(), GenerateTableRequest.class);
            if (request == null) {
                return errorReply(invalidBody("request is null"));
            }

            generatorValidator.verifyGenerateTableRequest(request);
            GenerateData generateData = generatorConverter.convertToGenerateData(request);
            Table table = generatorFacade.generate(generateData);
            byte[] file = fileTableService.generateFile(generateData.getFileType(), table);

            // Сообщение больше лимита брокер не примет, а запрос вернётся в очередь и будет выполняться снова и снова
            long maxFileSize = rabbitMqGenerationProperties.getMaxFileSizeKb() * BYTES_IN_KILOBYTE;
            if (file.length > maxFileSize) {
                return errorReply(new ErrorResponse(
                        ErrorConstants.FILE_SIZE_EXCEEDED_ERROR,
                        String.format(
                                "Generated file size = [%s] bytes is greater than max = [%s] bytes",
                                file.length, maxFileSize)));
            }

            return fileReply(file, generateData);
        } catch (IOException exception) {
            String cause = exception instanceof JsonProcessingException jsonException
                    ? jsonException.getOriginalMessage()
                    : exception.getMessage();
            return errorReply(invalidBody(cause));
        } catch (ConfigValidationException exception) {
            return errorReply(new GenerateDataErrorResponse(
                    ErrorConstants.Validation.REQUEST_FIELDS_INVALID_ERROR,
                    "Request fields have invalid states",
                    exception.getGeneralInfo(),
                    exception.getValidationColumnsInfo().getColumnNamesPerErrorMessages()));
        } catch (GenerationException exception) {
            return errorReply(new ErrorResponse(ErrorConstants.GENERATION_ERROR, exception.getMessage()));
        } catch (RuntimeException exception) {
            // Подробности остаются в логе: в ответ, как и по HTTP, внутреннее устройство сервиса не попадает
            log.error("Unexpected error while handling generation request from queue", exception);
            return errorReply(new ErrorResponse(
                    ErrorConstants.GENERATION_ERROR, "Unexpected error while handling generation request"));
        }
    }

    private Message fileReply(byte[] file, GenerateData generateData) {
        FileType fileType = generateData.getFileType();
        String fileName = StringUtils.defaultIfBlank(generateData.getTableName(), DEFAULT_FILE_NAME)
                + fileType.getExtensionWithDot();

        return MessageBuilder.withBody(file)
                .setContentType(fileType.getMimeType())
                .setHeader(STATUS_HEADER, ResponseStatus.SUCCESS.name())
                .setHeader(FILE_NAME_HEADER, fileName)
                .build();
    }

    private Message errorReply(ErrorResponse errorResponse) {
        log.warn(
                "Generation request from queue is rejected: code = [{}], message = [{}]",
                errorResponse.getCode(),
                errorResponse.getMessage());

        byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Can't serialize error response", exception);
        }

        return MessageBuilder.withBody(body)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setHeader(STATUS_HEADER, ResponseStatus.FAIL.name())
                .build();
    }

    private ErrorResponse invalidBody(String cause) {
        return new ErrorResponse(
                ErrorConstants.Validation.REQUEST_BODY_INVALID_ERROR,
                "Message body is not a valid generation request: " + cause);
    }
}
