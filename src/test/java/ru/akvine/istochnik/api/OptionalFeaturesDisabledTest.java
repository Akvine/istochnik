package ru.akvine.istochnik.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.compozit.commons.istochnik.ConfigDto;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.listeners.GenerationRequestListener;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.impl.GeneratorFacadeImpl;

@DisplayName("RabbitMQ and metrics are disabled by settings")
public class OptionalFeaturesDisabledTest extends ApiBaseTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private GeneratorFacade generatorFacade;

    @Test
    @DisplayName("Without metrics actuator endpoints are not available")
    void actuator_is_not_available_without_metrics() {
        RestAssured.given().when().get("/actuator").then().statusCode(HttpStatus.NOT_FOUND.value());
        RestAssured.given().when().get("/actuator/prometheus").then().statusCode(HttpStatus.NOT_FOUND.value());
        RestAssured.given().when().get("/actuator/health").then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Without metrics nothing is collected")
    void nothing_is_collected_without_metrics() {
        ColumnDto column = new ColumnDto()
                .setName("id")
                .setType(CustomType.UUID.getName())
                .setGenerationStrategy(GenerationStrategy.ALGORITHM.getName())
                .setConfig(new ConfigDto());
        sendGenerateRequest(new GenerateTableRequest()
                .setSize(1)
                .setFileType(FileType.CSV.name())
                .setColumns(List.of(column)));

        assertThat(generatorFacade).isInstanceOf(GeneratorFacadeImpl.class);
        assertThat(meterRegistry.getMeters()).isEmpty();
    }

    @Test
    @DisplayName("Without RabbitMQ there are no listeners and no queues to declare")
    void no_listeners_without_rabbitmq() {
        assertThat(applicationContext.getBeanNamesForType(GenerationRequestListener.class))
                .isEmpty();
        assertThat(applicationContext.getBeanNamesForType(Queue.class)).isEmpty();
        assertThat(rabbitListenerEndpointRegistry.getListenerContainers()).isEmpty();
    }
}
