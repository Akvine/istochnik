package ru.akvine.istochnik.exceptions;

/**
 * Выражение вычисляемой колонки не разобрано: синтаксическая ошибка или неподдерживаемая конструкция
 */
public class InvalidExpressionException extends GenerationException {
    public InvalidExpressionException(String message) {
        super(message);
    }
}
