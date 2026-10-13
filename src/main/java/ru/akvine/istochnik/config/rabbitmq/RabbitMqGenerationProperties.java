package ru.akvine.istochnik.config.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "rabbitmq.generation")
public class RabbitMqGenerationProperties {
    /**
     * Очередь, из которой сервис читает запросы на генерацию
     */
    private String requestQueue = "istochnik.generation.requests";

    /**
     * Очередь, в которую отправляется ответ, если в запросе нет адреса ответа (reply-to)
     */
    private String responseQueue = "istochnik.generation.responses";

    /**
     * Максимальный размер файла в килобайтах, который отправляется ответным сообщением
     */
    private int maxFileSizeKb = 10240;
}
