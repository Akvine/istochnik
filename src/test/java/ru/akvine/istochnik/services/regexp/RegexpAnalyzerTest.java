package ru.akvine.istochnik.services.regexp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.Random;
import nl.flotsam.xeger.Xeger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.akvine.istochnik.config.regexp.RegexpProperties;
import ru.akvine.istochnik.exceptions.InvalidRegexpException;

@DisplayName("Regexp analyzer tests")
class RegexpAnalyzerTest {
    // Ни одна проверка не должна занимать заметного времени: раньше на этих выражениях генерация зависала
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final RegexpAnalyzer analyzer = new RegexpAnalyzer(new RegexpProperties());

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "ORD-[0-9]{6}",
                "\\+7 \\(9[0-9]{2}\\) [0-9]{3}-[0-9]{2}-[0-9]{2}",
                "[A-Z]{3}-[0-9]{16}",
                "[a-z]+\\@[a-z]{3,8}\\.(com|ru)",
                ".*",
                "(ab|cd)*x?",
                "[a-z]{1000}",
                "[a-z]{2,}",
                "<1-100>",
                "<001-999>",
                "~(a{5})",
                "[a-z]*&[a-c]{3}",
                "\"quoted text\"",
                "#|a",
                // Детерминированный автомат на 1024 состояния - ещё в пределах лимита
                "[ab]*a[ab]{9}"
            })
    @DisplayName("Usual and boundary regexps are accepted")
    void accept_regexp(String regexp) {
        assertTimeoutPreemptively(
                TIMEOUT, () -> assertThatCode(() -> analyzer.verify(regexp)).doesNotThrowAnyException());
    }

    @Test
    @DisplayName("Accepted regexp is quickly built by generation library")
    void accepted_regexp_is_generated() {
        String regexp = "[ab]*a[ab]{9}";
        analyzer.verify(regexp);

        String value = assertTimeoutPreemptively(TIMEOUT, () -> new Xeger(regexp, new Random(1)).generate());
        assertThat(value).matches("[ab]*a[ab]{9}");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "[a-z]{1001}",
                "[a-z]{100000}",
                "[a-z]{2147483647}",
                "(a{100}){100}",
                "((a{100}){100}){100}",
                "[a-z]{500}[0-9]{501}",
                "(x|[a-z]{2000})",
                "\"text\"{300}"
            })
    @DisplayName("Regexp sets too long strings - error")
    void reject_too_long_result(String regexp) {
        assertRejected(regexp, "sets strings longer than max = [1000] characters");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "[ab]*a[ab]{10}",
                "[ab]*a[ab]{16}",
                "[ab]*a[ab]{20}",
                "(a|b)*a(a|b){40}",
                ".*a.{30}",
                "[ab]{0,30}a[ab]{30}",
                "(){2147483647}",
                "(#){1000000}"
            })
    @DisplayName("Regexp requires too large automaton - error instead of hanging")
    void reject_too_complex_regexp(String regexp) {
        assertRejected(regexp, "is too complex: generation requires more than max = [2000] automaton states");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"[a-z]{10000000000000000000}", "[a-z", "(abc", "a{5,", "<1-99999999999>"})
    @DisplayName("Regexp with syntax error - error")
    void reject_invalid_regexp(String regexp) {
        assertRejected(regexp, "is invalid");
    }

    @Test
    @DisplayName("Regexp is longer than max - error, long regexp is shortened in message")
    void reject_too_long_regexp() {
        String regexp = "a".repeat(1001);

        assertThatThrownBy(() -> analyzer.verify(regexp))
                .isInstanceOf(InvalidRegexpException.class)
                .hasMessage("regexp [" + "a".repeat(50) + "...] is longer than max = [1000] characters");
    }

    @Test
    @DisplayName("Regexp refers to named automaton - error")
    void reject_named_automaton() {
        assertRejected("<digits>", "refers to unknown automaton <digits>");
    }

    @Test
    @DisplayName("Limits are taken from settings")
    void limits_are_taken_from_settings() {
        RegexpProperties properties = new RegexpProperties();
        properties.setMaxLength(20);
        properties.setMaxResultLength(3);
        properties.setMaxAutomatonStates(3);
        RegexpAnalyzer customAnalyzer = new RegexpAnalyzer(properties);

        assertThatCode(() -> customAnalyzer.verify("ab")).doesNotThrowAnyException();
        assertThatThrownBy(() -> customAnalyzer.verify("a".repeat(21))).hasMessageContaining("max = [20] characters");
        assertThatThrownBy(() -> customAnalyzer.verify("a{4}")).hasMessageContaining("max = [3] characters");
        assertThatThrownBy(() -> customAnalyzer.verify("[ab]*a[ab]"))
                .hasMessageContaining("max = [3] automaton states");
    }

    @ParameterizedTest(name = "{0} - {1}")
    @CsvSource(
            delimiter = ';',
            value = {
                "ORD-[0-9]{6}; 1000000",
                "[A-C][0-9]{2}; 300",
                "(ab|cd)?; 3",
                "<001-999>; 999",
                "~(a{5})&a{1,6}; 5",
                // Строки, подходящие под обе части выражения, считаются один раз
                "[a-c]|[b-d]; 4",
                "(a|ab)(c|bcd); 4",
                "#; 0"
            })
    @DisplayName("Strings of finite regexp are counted exactly")
    void count_strings(String regexp, long expected) {
        assertThat(analyzer.countStrings(regexp)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"[a-z]+", "(ab|cd)*x?", ".*", "[a-z]{2,}", "[a-z]{1000}"})
    @DisplayName("Infinite regexp or regexp with more strings than long holds - max long")
    void count_strings_of_unbounded_regexp(String regexp) {
        assertTimeoutPreemptively(TIMEOUT, () -> assertThat(analyzer.countStrings(regexp))
                .isEqualTo(Long.MAX_VALUE));
    }

    @Test
    @DisplayName("Strings of invalid regexp are not counted - the same error as in verify")
    void count_strings_of_invalid_regexp() {
        assertTimeoutPreemptively(TIMEOUT, () -> assertThatThrownBy(() -> analyzer.countStrings("[ab]*a[ab]{20}"))
                .isInstanceOf(InvalidRegexpException.class)
                .hasMessageContaining("is too complex"));
    }

    private void assertRejected(String regexp, String reason) {
        assertTimeoutPreemptively(TIMEOUT, () -> assertThatThrownBy(() -> analyzer.verify(regexp))
                .isInstanceOf(InvalidRegexpException.class)
                .hasMessageStartingWith("regexp [")
                .hasMessageContaining(reason));
    }
}
