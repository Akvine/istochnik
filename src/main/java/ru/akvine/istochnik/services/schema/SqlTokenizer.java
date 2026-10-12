package ru.akvine.istochnik.services.schema;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Разбивает SQL-скрипт на лексемы. Комментарии пропускаются, а строки и имена в кавычках становятся одной лексемой,
 * поэтому скобки, запятые и точки с запятой внутри них на разбор не влияют
 */
@UtilityClass
class SqlTokenizer {
    private final String STATEMENT_SEPARATOR = ";";
    private final String LINE_COMMENT_START = "--";
    private final String BLOCK_COMMENT_START = "/*";
    private final String BLOCK_COMMENT_END = "*/";

    // Префиксы строковых литералов: E'...' (с экранированием обратной косой чертой), N'...', B'...', X'...'
    private final String STRING_PREFIXES = "ENBX";
    private final String ESCAPE_STRING_PREFIX = "E";

    private final int MAX_BRACKETS_CONTENT_LENGTH = 128;

    /**
     * @return операторы скрипта - лексемы между точками с запятой; пустые операторы пропускаются
     */
    List<List<SqlToken>> statements(String sql) {
        List<List<SqlToken>> statements = new ArrayList<>();
        List<SqlToken> statement = new ArrayList<>();
        for (SqlToken token : tokenize(sql)) {
            if (!token.isSymbol(STATEMENT_SEPARATOR)) {
                statement.add(token);
            } else if (!statement.isEmpty()) {
                statements.add(statement);
                statement = new ArrayList<>();
            }
        }

        if (!statement.isEmpty()) {
            statements.add(statement);
        }
        return statements;
    }

    List<SqlToken> tokenize(String sql) {
        List<SqlToken> tokens = new ArrayList<>();
        int length = sql.length();
        int position = 0;
        while (position < length) {
            char current = sql.charAt(position);
            if (Character.isWhitespace(current)) {
                position++;
            } else if (sql.startsWith(LINE_COMMENT_START, position)) {
                int lineEnd = sql.indexOf('\n', position);
                position = lineEnd < 0 ? length : lineEnd + 1;
            } else if (sql.startsWith(BLOCK_COMMENT_START, position)) {
                int commentEnd = sql.indexOf(BLOCK_COMMENT_END, position + BLOCK_COMMENT_START.length());
                position = commentEnd < 0 ? length : commentEnd + BLOCK_COMMENT_END.length();
            } else if (current == '\'') {
                position = readQuoted(sql, position, false, SqlToken.Kind.STRING, tokens);
            } else if (current == '"' || current == '`') {
                position = readQuoted(sql, position, false, SqlToken.Kind.QUOTED, tokens);
            } else if (current == '[') {
                position = readBrackets(sql, position, tokens);
            } else if (current == '$' && findDollarTagEnd(sql, position) >= 0) {
                position = readDollarQuoted(sql, position, tokens);
            } else if (isWordChar(current)) {
                position = readWord(sql, position, tokens);
            } else {
                tokens.add(new SqlToken(SqlToken.Kind.SYMBOL, String.valueOf(current)));
                position++;
            }
        }
        return tokens;
    }

    private int readWord(String sql, int start, List<SqlToken> tokens) {
        int position = start;
        while (position < sql.length() && isWordChar(sql.charAt(position))) {
            position++;
        }

        String word = sql.substring(start, position);
        boolean stringPrefix = word.length() == 1
                && STRING_PREFIXES.contains(word.toUpperCase())
                && position < sql.length()
                && sql.charAt(position) == '\'';
        if (stringPrefix) {
            boolean backslashEscapes = ESCAPE_STRING_PREFIX.equalsIgnoreCase(word);
            return readQuoted(sql, position, backslashEscapes, SqlToken.Kind.STRING, tokens);
        }

        tokens.add(new SqlToken(SqlToken.Kind.WORD, word));
        return position;
    }

    /**
     * Читает строку или имя в кавычках. Кавычка внутри записывается удвоением
     *
     * @param start позиция открывающей кавычки
     * @return позиция после закрывающей кавычки; если она не найдена - конец скрипта
     */
    private int readQuoted(String sql, int start, boolean backslashEscapes, SqlToken.Kind kind, List<SqlToken> tokens) {
        char quote = sql.charAt(start);
        StringBuilder text = new StringBuilder();
        int length = sql.length();
        int position = start + 1;
        while (position < length) {
            char current = sql.charAt(position);
            if (backslashEscapes && current == '\\' && position + 1 < length) {
                text.append(sql.charAt(position + 1));
                position += 2;
            } else if (current != quote) {
                text.append(current);
                position++;
            } else if (position + 1 < length && sql.charAt(position + 1) == quote) {
                text.append(quote);
                position += 2;
            } else {
                position++;
                break;
            }
        }

        tokens.add(new SqlToken(kind, text.toString()));
        return position;
    }

    /**
     * Квадратные скобки - это либо признак массива в типе PostgreSQL (integer[], integer[3]),
     * либо имя в стиле SQL Server ([dbo].[users])
     */
    private int readBrackets(String sql, int start, List<SqlToken> tokens) {
        int end = findBracketEnd(sql, start);
        if (end < 0) {
            tokens.add(new SqlToken(SqlToken.Kind.SYMBOL, String.valueOf(sql.charAt(start))));
            return start + 1;
        }

        String inner = sql.substring(start + 1, end).trim();
        if (inner.chars().allMatch(Character::isDigit)) {
            tokens.add(new SqlToken(SqlToken.Kind.SYMBOL, SqlToken.ARRAY_SYMBOL));
        } else {
            tokens.add(new SqlToken(SqlToken.Kind.QUOTED, inner));
        }
        return end + 1;
    }

    // Закрывающая скобка ищется только рядом с открывающей: имена и размеры массивов коротки, а поиск до конца
    // скрипта для каждой скобки сделал бы время разбора квадратичным
    private int findBracketEnd(String sql, int start) {
        int limit = Math.min(sql.length(), start + 1 + MAX_BRACKETS_CONTENT_LENGTH + 1);
        for (int position = start + 1; position < limit; position++) {
            if (sql.charAt(position) == ']') {
                return position;
            }
        }
        return -1;
    }

    /**
     * Строка PostgreSQL в долларовых кавычках: $$...$$ или $tag$...$tag$. В таких строках записывают тела функций,
     * точки с запятой внутри них операторы не разделяют
     */
    private int readDollarQuoted(String sql, int start, List<SqlToken> tokens) {
        int tagEnd = findDollarTagEnd(sql, start);
        String tag = sql.substring(start, tagEnd + 1);
        int close = sql.indexOf(tag, tagEnd + 1);
        int textEnd = close < 0 ? sql.length() : close;

        tokens.add(new SqlToken(SqlToken.Kind.STRING, sql.substring(tagEnd + 1, textEnd)));
        return close < 0 ? sql.length() : close + tag.length();
    }

    /**
     * @return позиция знака, закрывающего открывающий тег долларовой кавычки, или -1, если с этой позиции
     * строка в долларовых кавычках не начинается
     */
    private int findDollarTagEnd(String sql, int start) {
        int position = start + 1;
        // Тег не начинается с цифры: $1 - это номер параметра
        if (position < sql.length() && Character.isDigit(sql.charAt(position))) {
            return -1;
        }

        while (position < sql.length()
                && (Character.isLetterOrDigit(sql.charAt(position)) || sql.charAt(position) == '_')) {
            position++;
        }
        return position < sql.length() && sql.charAt(position) == '$' ? position : -1;
    }

    private boolean isWordChar(char value) {
        return Character.isLetterOrDigit(value) || value == '_' || value == '$' || value == '#';
    }
}
