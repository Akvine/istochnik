package ru.akvine.istochnik.services.schema;

/**
 * Лексема SQL-скрипта
 *
 * @param text текст лексемы; у строк и имён в кавычках - без кавычек
 */
record SqlToken(Kind kind, String text) {
    /**
     * Признак массива в типе колонки: integer[]
     */
    static final String ARRAY_SYMBOL = "[]";

    enum Kind {
        /**
         * Имя или ключевое слово без кавычек, число
         */
        WORD,
        /**
         * Имя в кавычках: "name", `name`, [name]
         */
        QUOTED,
        /**
         * Строковый литерал
         */
        STRING,
        /**
         * Знак: скобка, запятая, точка и т. п.
         */
        SYMBOL
    }

    boolean isWord(String keyword) {
        return kind == Kind.WORD && text.equalsIgnoreCase(keyword);
    }

    boolean isSymbol(String symbol) {
        return kind == Kind.SYMBOL && text.equals(symbol);
    }

    /**
     * Может ли лексема быть именем таблицы, колонки или типа
     */
    boolean isName() {
        return kind == Kind.WORD || kind == Kind.QUOTED;
    }
}
