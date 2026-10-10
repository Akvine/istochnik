package ru.akvine.istochnik.services.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.expression.spel.SpelEvaluationException;
import ru.akvine.istochnik.exceptions.InvalidExpressionException;

@DisplayName("Column expression parser tests")
public class ColumnExpressionParserTest {
    private final ColumnExpressionParser parser = new ColumnExpressionParser();

    @Test
    @DisplayName("Columns referenced as variables and through the row are collected separately")
    void dependencies_are_collected() {
        ColumnExpression expression =
                parser.parse("#lower(#row['first name']) + '.' + #last + (#age > 18 ? #row['first name'] : #last)");

        assertThat(expression.variables()).containsExactly("last", "age");
        assertThat(expression.rowKeys()).containsExactly("first name");
        assertThat(expression.dependencies()).containsExactly("last", "age", "first name");
    }

    @Test
    @DisplayName("Expression without references has no dependencies")
    void expression_without_references() {
        ColumnExpression expression = parser.parse("(1 + 2.5) * 3 >= 10 and !false ? 'big' : 'small'");

        assertThat(expression.dependencies()).isEmpty();
        assertThat(expression.expression().getValue()).isEqualTo("big");
    }

    @Test
    @DisplayName("Integer literals are long - arithmetic doesn't overflow at 2^31")
    void integer_literals_are_long() {
        assertThat(parser.parse("100000 * 100000").expression().getValue()).isEqualTo(10_000_000_000L);
        assertThat(parser.parse("-5 + 0x10").expression().getValue()).isEqualTo(11L);
        assertThat(parser.parse("7 / 2").expression().getValue()).isEqualTo(3L);
        // Цифры внутри строк литералами не считаются
        assertThat(parser.parse("'10 + 20' + 30").expression().getValue()).isEqualTo("10 + 2030");
    }

    @Test
    @DisplayName("Comparison, range and null-handling operators are supported")
    void operators_are_supported() {
        assertThat(parser.parse("5 between {1, 10} and !(11 between {1, 10})")
                        .expression()
                        .getValue())
                .isEqualTo(true);
        assertThat(parser.parse("'abc' < 'abd' and 2 != 3 or false")
                        .expression()
                        .getValue())
                .isEqualTo(true);
        assertThat(parser.parse("null ?: 'default'").expression().getValue()).isEqualTo("default");
        assertThat(parser.parse("-7 % 3 + 10 / 4 - 1.5").expression().getValue())
                .isEqualTo(-0.5);
    }

    @Test
    @DisplayName("Names of letters, digits and underscores are variables, other names are referenced through the row")
    void non_identifier_names_are_referenced_through_row() {
        assertThat(parser.parse("#first_name + #name2 + #_x").variables()).containsExactly("first_name", "name2", "_x");
        assertThat(parser.parse("#цена * #количество_1").variables()).containsExactly("цена", "количество_1");
        assertThat(parser.parse("#row['кол-во заказов'] + #row['1st']").rowKeys())
                .containsExactly("кол-во заказов", "1st");

        // Дефис читается как вычитание, имя не может начинаться с цифры
        assertThatThrownBy(() -> parser.parse("#first-name")).isInstanceOf(InvalidExpressionException.class);
        assertThatThrownBy(() -> parser.parse("#1st + 1")).isInstanceOf(InvalidExpressionException.class);
    }

    @Test
    @DisplayName("String multiplied by integer literal is an error, not a repeated string")
    void string_is_not_repeated() {
        ColumnExpression expression = parser.parse("'12.5' * 2");

        assertThatThrownBy(() -> expression.expression().getValue()).isInstanceOf(SpelEvaluationException.class);
    }

    @ParameterizedTest
    @DisplayName("Access to types, constructors, beans, methods, properties and assignment is rejected")
    @ValueSource(
            strings = {
                "T(java.lang.Runtime).getRuntime().exec('calc')",
                "new java.lang.String('x')",
                "@environment",
                "#name.length()",
                "#name.bytes",
                "#name?.bytes",
                "#name[0]",
                "#names.![#this]",
                "#names.?[#this > 1]",
                "#name = 5",
                "#count++",
                "#count ^ 2",
                "#phone matches '(a+)+$'",
                "#this",
                "#root",
                "{'a': 1}",
                "price * 2"
            })
    void unsupported_constructs_are_rejected(String expression) {
        assertThatThrownBy(() -> parser.parse(expression))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("unsupported");
    }

    @Test
    @DisplayName("Unsupported construct errors explain what to use instead")
    void unsupported_constructs_have_hints() {
        assertThatThrownBy(() -> parser.parse("price * 2")).hasMessageContaining("for example #price");
        assertThatThrownBy(() -> parser.parse("#count ^ 2")).hasMessageContaining("#pow(base, exponent)");
        assertThatThrownBy(() -> parser.parse("#name.length()")).hasMessageContaining("use functions");
    }

    @ParameterizedTest
    @DisplayName("Row variable can be used only with a column name in square brackets")
    @ValueSource(strings = {"#row", "#row[#name]", "#row[0]", "#row.name", "#row['a']['b']", "#row + 1"})
    void row_must_be_indexed_by_column_name(String expression) {
        assertThatThrownBy(() -> parser.parse(expression))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("#row['column name']");
    }

    @Test
    @DisplayName("Unknown function, wrong arguments count and function name used as column are rejected")
    void functions_are_verified() {
        assertThatThrownBy(() -> parser.parse("#exec('calc')"))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("function [#exec] is not supported")
                .hasMessageContaining("lower");
        assertThatThrownBy(() -> parser.parse("#round(#price)"))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("function [#round] takes [2] arguments, but [1] passed");
        assertThatThrownBy(() -> parser.parse("#lower + 'x'"))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("#row['lower']");
    }

    @ParameterizedTest
    @DisplayName("Syntax errors are reported")
    @ValueSource(strings = {"#price *", "(#price", "#price #qty", "'unclosed", "#price * 99999999999"})
    void syntax_errors_are_rejected(String expression) {
        assertThatThrownBy(() -> parser.parse(expression))
                .isInstanceOf(InvalidExpressionException.class)
                .hasMessageContaining("invalid syntax");
    }

    @Test
    @DisplayName("Only public methods of ExpressionFunctions are available as functions")
    void functions_registry() {
        assertThat(ColumnExpressionParser.FUNCTIONS.keySet())
                .containsExactly(
                        "abs",
                        "daysBetween",
                        "length",
                        "lower",
                        "max",
                        "min",
                        "plusDays",
                        "plusMonths",
                        "plusYears",
                        "pow",
                        "round",
                        "substring",
                        "toDouble",
                        "toInt",
                        "toStr",
                        "upper");
    }
}
