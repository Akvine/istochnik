package ru.akvine.istochnik.controllers.dto.validation;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
public class ValidationColumnsInfo {
    private final Map<String, List<String>> columnNamesPerErrorMessages = new HashMap<>();

    public void put(String columnName, String errorMessage) {
        put(columnName, Stream.of(errorMessage).collect(Collectors.toList()));
    }

    public void put(String columnName, List<String> errorMessages) {
        if (columnNamesPerErrorMessages.containsKey(columnName)) {
            columnNamesPerErrorMessages.get(columnName).addAll(errorMessages);
        } else {
            columnNamesPerErrorMessages.put(columnName, errorMessages);
        }
    }
}
