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
import ru.akvine.compozit.commons.utils.MathUtils;
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
                .map(value -> {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    } else {
                        return Long.parseLong(value);
                    }
                })
                .toList();
    }

    protected List<Double> asDouble(byte[] response) {
        return asDouble(response, 3);
    }

    protected List<Double> asDouble(byte[] response, int accuracy) {
        List<String> values = asString(response);
        return values.stream()
                .map(value -> {
                    if (StringUtils.isBlank(value)) {
                        return null;
                    } else {
                        return Double.parseDouble(value);
                    }
                })
                .map(value -> {
                    if (value == null) {
                        return null;
                    } else {
                        return MathUtils.round(value, accuracy);
                    }
                })
                .toList();
    }

    protected List<Boolean> asBoolean(byte[] response) {
        List<String> values = asString(response);
        return values.stream()
                .filter(StringUtils::isNotBlank)
                .map(Boolean::parseBoolean)
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

        return lines.subList(1, lines.size());
    }

    @Nullable
    public Boolean isRandomLong(List<Long> values) {
       return !isShiftedLong(values);
    }

    @Nullable
    public Boolean isRandomDouble(List<Double> values) {
        return !isShiftedDouble(values);
    }

    @Nullable
    public Boolean isShiftedLong(List<Long> values) {
        if (CollectionUtils.isEmpty(values) || values.size() == 1) {
            return null;
        }

        boolean shifted = true;
        long diff = values.get(1) - values.get(0);

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }

            if (values.get(i) - values.get(i - 1) != diff) {
                shifted = false;
                break;
            }
        }

        return shifted;
    }

    @Nullable
    public Boolean isShiftedDouble(List<Double> values) {
        if (CollectionUtils.isEmpty(values) || values.size() == 1) {
            return null;
        }

        boolean shifted = true;
        double diff = values.get(1) - values.get(0);

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) - values.get(i - 1) != diff) {
                shifted = false;
                break;
            }
        }

        return shifted;
    }
}
