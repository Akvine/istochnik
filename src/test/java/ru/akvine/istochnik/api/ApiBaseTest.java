package ru.akvine.istochnik.api;

import io.restassured.RestAssured;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ru.akvine.istochnik.IstochnikApplication;
import ru.akvine.istochnik.api.detectors.Detector;
import ru.akvine.istochnik.api.providers.DetectorsProvider;
import ru.akvine.istochnik.api.providers.TypeConvertersProvider;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

@SpringBootTest(classes = IstochnikApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public abstract class ApiBaseTest {
    @LocalServerPort
    private int port;

    private final TypeConvertersProvider typeConvertersProvider;
    private final DetectorsProvider detectorsProvider;

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
}
