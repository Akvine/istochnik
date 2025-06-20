package ru.akvine.istochnik.api.converters;

import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.enums.BaseType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public interface TypeConverter<T> {
    List<T> convert(byte[] response);

    default List<String> asString(byte[] response) {
        Asserts.isNotNull(response);
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

    BaseType getByType();
}
