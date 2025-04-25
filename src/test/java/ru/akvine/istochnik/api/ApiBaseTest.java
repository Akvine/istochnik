package ru.akvine.istochnik.api;

import io.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;
import ru.akvine.istochnik.IstochnikApplication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest(classes = IstochnikApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ApiBaseTest {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    protected List<Long> asLong(byte[] response) {
        List<String> values = asString(response);
        return values.stream()
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .toList();
    }

    protected List<String> asString(byte[] response) {
        List<String> lines;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(response), StandardCharsets.UTF_8))) {

            lines = reader.lines()
                    .map(line -> line.split(";", -1)[0])
                    .toList();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        if (lines.isEmpty()) {
            return List.of();
        }

        return lines.subList(1, lines.size());
    }

    @Nullable
    public Boolean isRandom(List<Long> values) {
       return !isShifted(values);
    }

    @Nullable
    public Boolean isShifted(List<Long> values) {
        if (CollectionUtils.isEmpty(values) || values.size() == 1) {
            return null;
        }

        boolean shifted = true;
        long diff = values.get(1) - values.get(0);

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) - values.get(i - 1) != diff) {
                shifted = false;
                break;
            }
        }

        return shifted;
    }
}
