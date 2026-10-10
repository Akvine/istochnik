package ru.akvine.istochnik.services.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionException;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.expression.spel.ast.CompoundExpression;
import org.springframework.expression.spel.ast.Elvis;
import org.springframework.expression.spel.ast.FloatLiteral;
import org.springframework.expression.spel.ast.FunctionReference;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.expression.spel.ast.InlineList;
import org.springframework.expression.spel.ast.IntLiteral;
import org.springframework.expression.spel.ast.LongLiteral;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.expression.spel.ast.OpDivide;
import org.springframework.expression.spel.ast.OpEQ;
import org.springframework.expression.spel.ast.OpGE;
import org.springframework.expression.spel.ast.OpGT;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.expression.spel.ast.OpLT;
import org.springframework.expression.spel.ast.OpMinus;
import org.springframework.expression.spel.ast.OpModulus;
import org.springframework.expression.spel.ast.OpMultiply;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.expression.spel.ast.OpPlus;
import org.springframework.expression.spel.ast.OperatorBetween;
import org.springframework.expression.spel.ast.OperatorNot;
import org.springframework.expression.spel.ast.OperatorPower;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.RealLiteral;
import org.springframework.expression.spel.ast.StringLiteral;
import org.springframework.expression.spel.ast.Ternary;
import org.springframework.expression.spel.ast.VariableReference;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.exceptions.InvalidExpressionException;

/**
 * Разбирает выражения вычисляемых колонок. Выражения приходят в запросе, поэтому из языка SpEL разрешена
 * только безопасная часть: литералы, ссылки на колонки, операторы и функции из {@link ExpressionFunctions}.
 * Обращения к типам, конструкторам, бинам, методам и свойствам отклоняются ещё до вычисления
 */
@Component
public class ColumnExpressionParser {
    /**
     * Функции, доступные в выражениях, по именам
     */
    public static final Map<String, Method> FUNCTIONS = Collections.unmodifiableMap(Arrays.stream(
                    ExpressionFunctions.class.getDeclaredMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .collect(Collectors.toMap(Method::getName, Function.identity(), (first, second) -> first, TreeMap::new)));

    private static final Set<Class<? extends SpelNode>> SUPPORTED_NODES = Set.of(
            LongLiteral.class,
            RealLiteral.class,
            FloatLiteral.class,
            StringLiteral.class,
            BooleanLiteral.class,
            NullLiteral.class,
            OpPlus.class,
            OpMinus.class,
            OpMultiply.class,
            OpDivide.class,
            OpModulus.class,
            OpEQ.class,
            OpNE.class,
            OpLT.class,
            OpLE.class,
            OpGT.class,
            OpGE.class,
            OpAnd.class,
            OpOr.class,
            OperatorNot.class,
            OperatorBetween.class,
            Ternary.class,
            Elvis.class,
            InlineList.class);

    private static final Set<String> RESERVED_VARIABLES = Set.of("this", "root");
    private static final int MAX_CONSTRUCT_LENGTH = 50;

    // Компиляция выражений в байт-код отключена: выражения приходят извне и живут один запрос
    private final SpelExpressionParser parser =
            new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.OFF, null));

    /**
     * @throws InvalidExpressionException если выражение содержит синтаксическую ошибку или неподдерживаемую конструкцию
     */
    public ColumnExpression parse(String source) {
        SpelExpression expression = parseRaw(source);

        Set<String> variables = new LinkedHashSet<>();
        Set<String> rowKeys = new LinkedHashSet<>();
        List<Integer> intLiteralEnds = new ArrayList<>();
        analyze(expression.getAST(), variables, rowKeys, intLiteralEnds);

        if (!intLiteralEnds.isEmpty()) {
            expression = parseRaw(withLongLiterals(source, intLiteralEnds));
        }

        return new ColumnExpression(
                source, expression, Collections.unmodifiableSet(variables), Collections.unmodifiableSet(rowKeys));
    }

    private SpelExpression parseRaw(String source) {
        try {
            return parser.parseRaw(source);
        } catch (ExpressionException | IllegalStateException exception) {
            throw new InvalidExpressionException("field [expression] has invalid syntax: " + exception.getMessage());
        }
    }

    private void analyze(SpelNode node, Set<String> variables, Set<String> rowKeys, List<Integer> intLiteralEnds) {
        switch (node) {
            case CompoundExpression compound -> rowKeys.add(extractRowKey(compound));
            case VariableReference variable -> variables.add(extractVariableName(variable));
            case IntLiteral literal -> intLiteralEnds.add(literal.getEndPosition());
            case FunctionReference function -> {
                verifyFunction(function);
                analyzeChildren(function, variables, rowKeys, intLiteralEnds);
            }
            default -> {
                if (!SUPPORTED_NODES.contains(node.getClass())) {
                    throw unsupported(node);
                }
                analyzeChildren(node, variables, rowKeys, intLiteralEnds);
            }
        }
    }

    private void analyzeChildren(
            SpelNode node, Set<String> variables, Set<String> rowKeys, List<Integer> intLiteralEnds) {
        for (int i = 0; i < node.getChildCount(); i++) {
            analyze(node.getChild(i), variables, rowKeys, intLiteralEnds);
        }
    }

    // Из составных выражений разрешено только обращение к колонке по имени: #row['column name']
    private String extractRowKey(CompoundExpression compound) {
        if (compound.getChildCount() == 2
                && isRowVariable(compound.getChild(0))
                && compound.getChild(1) instanceof Indexer indexer
                && indexer.getChildCount() == 1
                && indexer.getChild(0) instanceof StringLiteral columnName) {
            return (String) columnName.getLiteralValue().getValue();
        }

        if (isRowVariable(compound.getChild(0))) {
            throw new InvalidExpressionException(rowUsageError());
        }
        throw unsupported(compound);
    }

    private String extractVariableName(VariableReference variable) {
        String name = variable.toStringAST().substring(1);

        if (ColumnExpression.ROW_VARIABLE.equals(name)) {
            throw new InvalidExpressionException(rowUsageError());
        }
        if (RESERVED_VARIABLES.contains(name)) {
            throw unsupported(variable);
        }
        if (FUNCTIONS.containsKey(name)) {
            throw new InvalidExpressionException(String.format(
                    "[#%s] is a function. To reference a column with this name use #%s['%s']",
                    name, ColumnExpression.ROW_VARIABLE, name));
        }

        return name;
    }

    private void verifyFunction(FunctionReference function) {
        String name = StringUtils.substringBetween(function.toStringAST(), "#", "(");

        Method method = FUNCTIONS.get(name);
        if (method == null) {
            throw new InvalidExpressionException(String.format(
                    "function [#%s] is not supported. Available functions: %s", name, FUNCTIONS.keySet()));
        }
        if (method.getParameterCount() != function.getChildCount()) {
            throw new InvalidExpressionException(String.format(
                    "function [#%s] takes [%s] arguments, but [%s] passed",
                    name, method.getParameterCount(), function.getChildCount()));
        }
    }

    private boolean isRowVariable(SpelNode node) {
        return node instanceof VariableReference && ("#" + ColumnExpression.ROW_VARIABLE).equals(node.toStringAST());
    }

    private String rowUsageError() {
        return String.format(
                "[#%s] must be followed by a column name in square brackets: #%s['column name']",
                ColumnExpression.ROW_VARIABLE, ColumnExpression.ROW_VARIABLE);
    }

    private InvalidExpressionException unsupported(SpelNode node) {
        String construct = StringUtils.abbreviate(node.toStringAST(), MAX_CONSTRUCT_LENGTH);

        String hint = "";
        if (node instanceof PropertyOrFieldReference) {
            hint = ". A column is referenced with a leading #, for example #" + construct;
        } else if (node instanceof OperatorPower) {
            hint = ". Use function #pow(base, exponent)";
        } else if (node instanceof CompoundExpression) {
            hint = ". Methods and properties are not available, use functions: " + FUNCTIONS.keySet();
        }

        return new InvalidExpressionException(
                "field [expression] contains unsupported construct [" + construct + "]" + hint);
    }

    // Целые литералы становятся длинными, как и значения целочисленных колонок. Иначе арифметика над литералами
    // переполнялась бы на 2^31, а умножение строки на целый литерал повторяло бы строку вместо ошибки
    private String withLongLiterals(String source, List<Integer> intLiteralEnds) {
        StringBuilder normalized = new StringBuilder(source);
        // Вставка идёт с конца, чтобы не сдвигать ещё не обработанные позиции
        intLiteralEnds.stream().sorted(Comparator.reverseOrder()).forEach(end -> normalized.insert((int) end, "L"));
        return normalized.toString();
    }
}
