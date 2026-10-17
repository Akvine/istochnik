package ru.akvine.istochnik.services.regexp;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicAutomata;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.config.regexp.RegexpProperties;
import ru.akvine.istochnik.exceptions.InvalidRegexpException;

/**
 * Проверяет выражение стратегии regexp до генерации.
 * Библиотека генерации строит по выражению минимальный детерминированный автомат, и на некоторых выражениях
 * он растёт без меры: повтор {100000} разворачивается в сто тысяч копий, а короткое [ab]*a[ab]{20} даёт
 * миллионы состояний. Прервать построение нельзя, поэтому здесь автомат строится теми же операциями, что и
 * в библиотеке, но перед каждой операцией, которая может его раздуть, оценивается число состояний.
 * Дерево разобранного выражения библиотека не открывает, поэтому его поля читаются через reflection
 */
@Component
public class RegexpAnalyzer {
    private static final int SHOWN_REGEXP_LENGTH = 50;

    private final RegexpProperties regexpProperties;

    private final Field kind;
    private final Field exp1;
    private final Field exp2;
    private final Field string;
    private final Field character;
    private final Field min;
    private final Field max;
    private final Field digits;
    private final Field from;
    private final Field to;

    // Поля читаются при создании бина: если версия библиотеки другая, приложение не запустится
    public RegexpAnalyzer(RegexpProperties regexpProperties) {
        this.regexpProperties = regexpProperties;
        this.kind = field("kind");
        this.exp1 = field("exp1");
        this.exp2 = field("exp2");
        this.string = field("s");
        this.character = field("c");
        this.min = field("min");
        this.max = field("max");
        this.digits = field("digits");
        this.from = field("from");
        this.to = field("to");
    }

    /**
     * @throws InvalidRegexpException если выражение длиннее допустимого, с ошибкой, задаёт слишком длинные
     * строки или требует слишком большого автомата
     */
    public void verify(String regexp) {
        if (regexp.length() > regexpProperties.getMaxLength()) {
            throw invalid(regexp, "is longer than max = [" + regexpProperties.getMaxLength() + "] characters");
        }

        RegExp parsed;
        try {
            parsed = new RegExp(regexp);
        } catch (IllegalArgumentException exception) {
            throw invalid(regexp, "is invalid: " + exception.getMessage());
        }

        if (maxLength(parsed) > regexpProperties.getMaxResultLength()) {
            throw invalid(
                    regexp,
                    "sets strings longer than max = [" + regexpProperties.getMaxResultLength() + "] characters");
        }

        build(regexp, parsed);
    }

    /**
     * Длина самой длинной строки выражения. Повтор без верхней границы считается одним повтором
     * или минимальным числом повторов, если оно задано
     */
    private long maxLength(RegExp node) {
        return switch (kindOf(node)) {
            case "REGEXP_UNION" -> Math.max(maxLength(child(node, exp1)), maxLength(child(node, exp2)));
            case "REGEXP_CONCATENATION" -> add(maxLength(child(node, exp1)), maxLength(child(node, exp2)));
            case "REGEXP_INTERSECTION" -> Math.min(maxLength(child(node, exp1)), maxLength(child(node, exp2)));
            case "REGEXP_OPTIONAL", "REGEXP_REPEAT", "REGEXP_COMPLEMENT" -> maxLength(child(node, exp1));
            case "REGEXP_REPEAT_MIN" -> multiply(maxLength(child(node, exp1)), Math.max(number(node, min), 1));
            case "REGEXP_REPEAT_MINMAX" -> multiply(maxLength(child(node, exp1)), number(node, max));
            case "REGEXP_STRING" -> text(node).length();
            case "REGEXP_EMPTY" -> 0;
            case "REGEXP_INTERVAL" -> number(node, digits) > 0
                    ? number(node, digits)
                    : String.valueOf(number(node, max)).length();
            default -> 1;
        };
    }

    /**
     * Строит автомат так же, как библиотека генерации: каждый узел выражения сразу минимизируется
     */
    private Automaton build(String regexp, RegExp node) {
        return switch (kindOf(node)) {
            case "REGEXP_UNION" -> minimize(regexp, Automaton.union(leaves(regexp, node, "REGEXP_UNION")));
            case "REGEXP_CONCATENATION" -> minimize(
                    regexp, Automaton.concatenate(leaves(regexp, node, "REGEXP_CONCATENATION")));
            case "REGEXP_INTERSECTION" -> {
                Automaton first = build(regexp, child(node, exp1));
                Automaton second = build(regexp, child(node, exp2));
                // Пересечение строится как произведение автоматов
                checkStates(regexp, (long) first.getNumberOfStates() * second.getNumberOfStates());
                yield minimize(regexp, first.intersection(second));
            }
            case "REGEXP_OPTIONAL" -> minimize(
                    regexp, build(regexp, child(node, exp1)).optional());
            case "REGEXP_REPEAT" -> minimize(
                    regexp, build(regexp, child(node, exp1)).repeat());
            case "REGEXP_REPEAT_MIN" -> {
                Automaton repeated = build(regexp, child(node, exp1));
                int count = number(node, min);
                // Повтор - это столько копий автомата, сколько повторов
                checkStates(regexp, repeated.getNumberOfStates() * ((long) count + 1));
                yield minimize(regexp, repeated.repeat(count));
            }
            case "REGEXP_REPEAT_MINMAX" -> {
                Automaton repeated = build(regexp, child(node, exp1));
                checkStates(regexp, (long) repeated.getNumberOfStates() * number(node, max));
                yield minimize(regexp, repeated.repeat(number(node, min), number(node, max)));
            }
            case "REGEXP_COMPLEMENT" -> minimize(
                    regexp, build(regexp, child(node, exp1)).complement());
            case "REGEXP_CHAR" -> BasicAutomata.makeChar(symbol(node, character));
            case "REGEXP_CHAR_RANGE" -> BasicAutomata.makeCharRange(symbol(node, from), symbol(node, to));
            case "REGEXP_ANYCHAR" -> BasicAutomata.makeAnyChar();
            case "REGEXP_EMPTY" -> BasicAutomata.makeEmpty();
            case "REGEXP_STRING" -> BasicAutomata.makeString(text(node));
            case "REGEXP_ANYSTRING" -> BasicAutomata.makeAnyString();
            case "REGEXP_INTERVAL" -> {
                try {
                    yield BasicAutomata.makeInterval(number(node, min), number(node, max), number(node, digits));
                } catch (IllegalArgumentException exception) {
                    throw invalid(regexp, "is invalid: " + exception.getMessage());
                }
            }
                // Именованные автоматы библиотеке генерации не передаются, и такое выражение она не построит
            case "REGEXP_AUTOMATON" -> throw invalid(regexp, "refers to unknown automaton <" + text(node) + ">");
            default -> throw new IllegalStateException("Unsupported regexp node kind = [" + kindOf(node) + "]");
        };
    }

    // Подряд идущие объединения и склейки библиотека собирает в одну операцию
    private List<Automaton> leaves(String regexp, RegExp node, String nodeKind) {
        List<Automaton> leaves = new ArrayList<>();
        Deque<RegExp> nodes = new ArrayDeque<>();
        nodes.push(node);
        while (!nodes.isEmpty()) {
            RegExp current = nodes.pop();
            if (nodeKind.equals(kindOf(current))) {
                nodes.push(child(current, exp2));
                nodes.push(child(current, exp1));
            } else {
                leaves.add(build(regexp, current));
            }
        }

        return leaves;
    }

    private Automaton minimize(String regexp, Automaton automaton) {
        checkStates(regexp, automaton.getNumberOfStates());
        // Минимизация начинается с детерминизации, на которой автомат и разрастается
        if (!automaton.isDeterministic()) {
            countDeterminizedStates(regexp, automaton);
        }

        automaton.minimize();
        return automaton;
    }

    /**
     * Детерминизация построением подмножеств, но без самого автомата: считаются только состояния,
     * и подсчёт прерывается, как только их становится больше допустимого
     */
    private void countDeterminizedStates(String regexp, Automaton automaton) {
        Set<Set<State>> found = new HashSet<>();
        Deque<Set<State>> queue = new ArrayDeque<>();
        Set<State> initial = Set.of(automaton.getInitialState());
        found.add(initial);
        queue.add(initial);

        while (!queue.isEmpty()) {
            Set<State> current = queue.poll();

            // Границы интервалов символов, внутри которых переходы из подмножества одни и те же
            TreeSet<Integer> bounds = new TreeSet<>();
            for (State state : current) {
                for (Transition transition : state.getTransitions()) {
                    bounds.add((int) transition.getMin());
                    bounds.add(transition.getMax() + 1);
                }
            }

            Integer previous = null;
            for (Integer bound : bounds) {
                if (previous != null) {
                    Set<State> target = step(current, previous, bound - 1);
                    if (!target.isEmpty() && found.add(target)) {
                        checkStates(regexp, found.size());
                        queue.add(target);
                    }
                }
                previous = bound;
            }
        }
    }

    private Set<State> step(Set<State> states, int first, int last) {
        Set<State> target = new HashSet<>();
        for (State state : states) {
            for (Transition transition : state.getTransitions()) {
                if (transition.getMin() <= first && last <= transition.getMax()) {
                    target.add(transition.getDest());
                }
            }
        }

        return target;
    }

    private void checkStates(String regexp, long states) {
        if (states > regexpProperties.getMaxAutomatonStates()) {
            throw invalid(
                    regexp,
                    "is too complex: generation requires more than max = [" + regexpProperties.getMaxAutomatonStates()
                            + "] automaton states");
        }
    }

    private InvalidRegexpException invalid(String regexp, String reason) {
        String shown =
                regexp.length() > SHOWN_REGEXP_LENGTH ? regexp.substring(0, SHOWN_REGEXP_LENGTH) + "..." : regexp;
        return new InvalidRegexpException("regexp [" + shown + "] " + reason);
    }

    private static long add(long first, long second) {
        return first > Long.MAX_VALUE - second ? Long.MAX_VALUE : first + second;
    }

    private static long multiply(long value, long count) {
        return value != 0 && count > Long.MAX_VALUE / value ? Long.MAX_VALUE : value * count;
    }

    private String kindOf(RegExp node) {
        return ((Enum<?>) read(node, kind)).name();
    }

    private RegExp child(RegExp node, Field field) {
        return (RegExp) read(node, field);
    }

    private String text(RegExp node) {
        return (String) read(node, string);
    }

    private int number(RegExp node, Field field) {
        return (int) read(node, field);
    }

    private char symbol(RegExp node, Field field) {
        return (char) read(node, field);
    }

    private static Object read(RegExp node, Field field) {
        try {
            return field.get(node);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Can't read field [" + field.getName() + "] of regexp", exception);
        }
    }

    private static Field field(String name) {
        try {
            Field field = RegExp.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException exception) {
            throw new IllegalStateException(
                    "Unsupported version of dk.brics.automaton: regexp has no field [" + name + "]", exception);
        }
    }
}
