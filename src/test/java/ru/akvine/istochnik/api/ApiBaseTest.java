package ru.akvine.istochnik.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.IstochnikApplication;
import ru.akvine.istochnik.api.common.configs.RestMethods;
import ru.akvine.istochnik.api.common.providers.DetectorsProvider;
import ru.akvine.istochnik.api.common.providers.TypeConvertersProvider;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = IstochnikApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")

@ComponentScan({
        "ru.akvine.istochnik"
})
public abstract class ApiBaseTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TypeConvertersProvider typeConvertersProvider;
    @Autowired
    private DetectorsProvider detectorsProvider;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    protected List<?> convert(BaseType type, byte[] response) {
        return typeConvertersProvider.get(type).convert(response);
    }

    protected boolean isRandom(BaseType type, List values) {
        return detectorsProvider.get(type).isRandom(values);
    }

    protected boolean isShifted(BaseType type, List values) {
        return detectorsProvider.get(type).isShifted(values);
    }

    protected byte[] sendGenerateRequest(GenerateTableRequest request) {
        return RestAssured
                .given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(RestMethods.GENERATE_DATA_ENDPOINT)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .asByteArray();
    }
}
