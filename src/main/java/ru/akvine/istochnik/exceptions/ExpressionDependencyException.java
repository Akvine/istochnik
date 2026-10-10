package ru.akvine.istochnik.exceptions;

import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Для вычисляемых колонок нельзя построить порядок вычисления: ссылка на неизвестную колонку или цикл
 */
@Getter
public class ExpressionDependencyException extends GenerationException {
    /**
     * Индекс колонки в запросе и найденные в её выражении ошибки
     */
    private final Map<Integer, List<String>> errors;

    public ExpressionDependencyException(Map<Integer, List<String>> errors) {
        super("Expression columns have invalid dependencies: " + errors.values());
        this.errors = errors;
    }
}
