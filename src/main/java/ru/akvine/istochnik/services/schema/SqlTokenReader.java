package ru.akvine.istochnik.services.schema;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Последовательное чтение лексем одного оператора или его части
 */
final class SqlTokenReader {
    private static final String GROUP_START = "(";
    private static final String GROUP_END = ")";
    private static final String NAME_SEPARATOR = ".";
    private static final String LIST_SEPARATOR = ",";

    private final List<SqlToken> tokens;
    private int position;

    SqlTokenReader(List<SqlToken> tokens) {
        this.tokens = tokens;
    }

    /**
     * Делит лексемы по запятым верхнего уровня: запятые внутри скобок части не разделяют
     */
    static List<List<SqlToken>> splitByComma(List<SqlToken> tokens) {
        List<List<SqlToken>> parts = new ArrayList<>();
        List<SqlToken> part = new ArrayList<>();
        int depth = 0;
        for (SqlToken token : tokens) {
            if (token.isSymbol(GROUP_START)) {
                depth++;
            } else if (token.isSymbol(GROUP_END)) {
                depth--;
            }

            if (depth == 0 && token.isSymbol(LIST_SEPARATOR)) {
                parts.add(part);
                part = new ArrayList<>();
            } else {
                part.add(token);
            }
        }

        if (!part.isEmpty()) {
            parts.add(part);
        }
        return parts;
    }

    boolean hasNext() {
        return position < tokens.size();
    }

    @Nullable
    SqlToken peek() {
        return peek(0);
    }

    @Nullable
    SqlToken peek(int offset) {
        int index = position + offset;
        return index < tokens.size() ? tokens.get(index) : null;
    }

    SqlToken next() {
        return tokens.get(position++);
    }

    boolean isWord(String keyword) {
        SqlToken token = peek();
        return token != null && token.isWord(keyword);
    }

    boolean isGroupStart() {
        SqlToken token = peek();
        return token != null && token.isSymbol(GROUP_START);
    }

    boolean skipWord(String keyword) {
        if (isWord(keyword)) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * Пропускает ключевые слова, только если они идут подряд все
     */
    boolean skipWords(String... keywords) {
        for (int i = 0; i < keywords.length; i++) {
            SqlToken token = peek(i);
            if (token == null || !token.isWord(keywords[i])) {
                return false;
            }
        }

        position += keywords.length;
        return true;
    }

    /**
     * Читает имя, возможно составное (schema.table). Кавычки в результат не попадают
     *
     * @return null, если указатель стоит не на имени
     */
    @Nullable
    String readName() {
        SqlToken token = peek();
        if (token == null || !token.isName()) {
            return null;
        }

        StringBuilder name = new StringBuilder(next().text());
        while (hasNamePart()) {
            next();
            name.append(NAME_SEPARATOR).append(next().text());
        }
        return name.toString();
    }

    /**
     * Читает содержимое скобок, на открывающей из которых стоит указатель. Вложенные скобки остаются в результате
     */
    List<SqlToken> readGroup() {
        List<SqlToken> group = new ArrayList<>();
        int depth = 0;
        while (hasNext()) {
            SqlToken token = next();
            if (token.isSymbol(GROUP_START)) {
                if (depth > 0) {
                    group.add(token);
                }
                depth++;
            } else if (token.isSymbol(GROUP_END)) {
                depth--;
                if (depth == 0) {
                    break;
                }
                group.add(token);
            } else {
                group.add(token);
            }
        }
        return group;
    }

    /**
     * Возвращает все непрочитанные лексемы
     */
    List<SqlToken> rest() {
        List<SqlToken> rest = tokens.subList(position, tokens.size());
        position = tokens.size();
        return rest;
    }

    private boolean hasNamePart() {
        SqlToken separator = peek();
        SqlToken part = peek(1);
        return separator != null && separator.isSymbol(NAME_SEPARATOR) && part != null && part.isName();
    }
}
