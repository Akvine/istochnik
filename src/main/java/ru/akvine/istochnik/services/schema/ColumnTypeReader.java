package ru.akvine.istochnik.services.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Читает тип колонки: имя из одного или нескольких слов, аргументы в скобках и признак массива
 */
@UtilityClass
class ColumnTypeReader {
    private final String ARRAY_KEYWORD = "ARRAY";
    private final String NAME_SEPARATOR = ".";

    // Слова, с которых начинаются ограничения и свойства колонки: на них описание типа заканчивается
    private final Set<String> TYPE_END_KEYWORDS = Set.of(
            "NOT",
            "NULL",
            "DEFAULT",
            "PRIMARY",
            "UNIQUE",
            "REFERENCES",
            "CHECK",
            "CONSTRAINT",
            "GENERATED",
            "COLLATE",
            "AUTO_INCREMENT",
            "AUTOINCREMENT",
            "IDENTITY",
            "COMMENT",
            "ON",
            "AS",
            "KEY",
            "CHARSET",
            "VISIBLE",
            "INVISIBLE",
            "STORAGE",
            "COMPRESSION");

    /**
     * Разбирает тип, записанный отдельной строкой, например в атрибуте type журнала изменений Liquibase
     */
    ColumnType parse(String definition) {
        SqlTokenReader reader = new SqlTokenReader(SqlTokenizer.tokenize(definition));
        ColumnType type = read(reader);

        // Строка разобрана не целиком - это не тип SQL (например, подстановка ${type}): имя оставляется как есть
        if (reader.hasNext() || type.name().isEmpty()) {
            return new ColumnType(definition.trim().toLowerCase(Locale.ROOT), List.of(), false);
        }
        return type;
    }

    /**
     * Читает тип с текущей позиции и останавливается на первой лексеме, которая к типу не относится
     */
    ColumnType read(SqlTokenReader reader) {
        List<String> words = new ArrayList<>();
        List<String> arguments = List.of();
        boolean argumentsRead = false;
        boolean array = false;
        boolean qualified = false;

        while (reader.hasNext()) {
            SqlToken token = reader.peek();
            if (reader.isGroupStart()) {
                if (words.isEmpty() || argumentsRead) {
                    break;
                }
                arguments = readArguments(reader);
                argumentsRead = true;
            } else if (token.isSymbol(SqlToken.ARRAY_SYMBOL)) {
                array = true;
                reader.next();
            } else if (token.isSymbol(NAME_SEPARATOR) && !words.isEmpty()) {
                qualified = true;
                reader.next();
            } else if (!token.isName() || isTypeEnd(reader)) {
                break;
            } else if (token.isWord(ARRAY_KEYWORD)) {
                array = true;
                reader.next();
            } else {
                reader.next();
                // Имя типа в кавычках чувствительно к регистру, без кавычек - нет
                String word = token.kind() == SqlToken.Kind.WORD ? token.text().toLowerCase(Locale.ROOT) : token.text();
                if (qualified) {
                    int last = words.size() - 1;
                    words.set(last, words.get(last) + NAME_SEPARATOR + word);
                    qualified = false;
                } else {
                    words.add(word);
                }
            }
        }

        return new ColumnType(String.join(" ", words), arguments, array);
    }

    private List<String> readArguments(SqlTokenReader reader) {
        return SqlTokenReader.splitByComma(reader.readGroup()).stream()
                .map(argument -> argument.stream().map(SqlToken::text).collect(Collectors.joining(" ")))
                .toList();
    }

    private boolean isTypeEnd(SqlTokenReader reader) {
        SqlToken token = reader.peek();
        if (token.kind() != SqlToken.Kind.WORD) {
            return false;
        }

        // CHARACTER - начало типа character varying, но CHARACTER SET - уже свойство колонки (MySQL)
        SqlToken following = reader.peek(1);
        if (token.isWord("CHARACTER") && following != null && following.isWord("SET")) {
            return true;
        }
        return TYPE_END_KEYWORDS.contains(token.text().toUpperCase(Locale.ROOT));
    }
}
