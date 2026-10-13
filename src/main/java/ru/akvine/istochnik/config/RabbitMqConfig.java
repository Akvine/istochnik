package ru.akvine.istochnik.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.config.rabbitmq.RabbitMqGenerationProperties;

@Configuration
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true")
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final RabbitMqGenerationProperties rabbitMqGenerationProperties;

    @Bean
    public Queue generationRequestQueue() {
        return new Queue(rabbitMqGenerationProperties.getRequestQueue());
    }

    // Очередь объявляется заранее: сообщение, отправленное в несуществующую очередь, брокер молча отбрасывает
    @Bean
    public Queue generationResponseQueue() {
        return new Queue(rabbitMqGenerationProperties.getResponseQueue());
    }
}
