package ru.akvine.istochnik.services.expression;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.expression.Expression;

/**
 * Разобранное выражение вычисляемой колонки
 *
 * @param source     исходный текст выражения
 * @param expression выражение, готовое к вычислению
 * @param variables  колонки, на которые выражение ссылается как на переменные: #name
 * @param rowKeys    колонки, на которые выражение ссылается через строку таблицы: #row['name']
 */
public record ColumnExpression(String source, Expression expression, Set<String> variables, Set<String> rowKeys) {
    public static final String ROW_VARIABLE = "row";

    /**
     * Имена всех колонок, значения которых нужны для вычисления
     */
    public Set<String> dependencies() {
        Set<String> dependencies = new LinkedHashSet<>(variables);
        dependencies.addAll(rowKeys);
        return Collections.unmodifiableSet(dependencies);
    }
}
