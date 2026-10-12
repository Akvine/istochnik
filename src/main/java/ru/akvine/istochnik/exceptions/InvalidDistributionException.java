package ru.akvine.istochnik.exceptions;

import java.util.List;
import lombok.Getter;

/**
 * Распределение значений колонки описано с ошибками
 */
@Getter
public class InvalidDistributionException extends GenerationException {
    private final List<String> errors;

    public InvalidDistributionException(List<String> errors) {
        super(String.join(". ", errors));
        this.errors = errors;
    }
}
