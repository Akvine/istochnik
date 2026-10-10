package ru.akvine.istochnik.services;

import java.util.List;
import java.util.Map;
import ru.akvine.istochnik.exceptions.ExpressionDependencyException;
import ru.akvine.istochnik.exceptions.InvalidExpressionException;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.expression.ColumnExpression;
import ru.akvine.istochnik.services.expression.SourceColumn;

/**
 * Вычисляемые колонки: значения считаются выражением по другим колонкам той же строки
 */
public interface ExpressionColumnService {
    /**
     * Включена ли стратегия expression в настройках приложения
     */
    boolean isEnabled();

    /**
     * @throws InvalidExpressionException если выражение пустое, слишком длинное или содержит ошибку
     */
    ColumnExpression parse(String expression);

    /**
     * Определяет порядок вычисления колонок по ссылкам в их выражениях
     *
     * @param columnNames имена всех колонок таблицы в порядке запроса
     * @param expressions выражения вычисляемых колонок по индексам этих колонок в запросе
     * @return уровни из индексов вычисляемых колонок: колонки одного уровня зависят только от обычных колонок
     * и от колонок предыдущих уровней
     * @throws ExpressionDependencyException если выражение ссылается на неизвестную колонку или ссылки образуют цикл
     */
    List<List<Integer>> plan(List<String> columnNames, Map<Integer, ColumnExpression> expressions);

    /**
     * @param sources готовые колонки, на которые ссылается выражение, по именам
     */
    List<?> calculate(GenerateColumn column, ColumnExpression expression, Map<String, SourceColumn> sources);
}
