package ru.akvine.istochnik.services.distribution;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.akvine.compozit.commons.utils.DateTimeUtils;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.utils.ZonedDateTimeUtils;

/**
 * Числовая шкала типа данных, для которого можно задать распределение. По положению значений на шкале параметры
 * распределения (среднее, стандартное отклонение) переводятся в доли ширины диапазона. Единица шкалы совпадает
 * с единицей шага типа: для чисел - единица значения, для дат - день, для времени - секунда
 */
@Getter
@RequiredArgsConstructor
public enum DistributionAxis {
    INTEGER(BaseType.INTEGER.getValue(), true, value -> Long.parseLong(value.trim())),
    DOUBLE(BaseType.DOUBLE.getValue(), false, value -> Double.parseDouble(value.trim())),
    DECIMAL(CustomType.DECIMAL.getName(), false, value -> new BigDecimal(value.trim()).doubleValue()),
    DATE(CustomType.DATE.getName(), true, value -> DateTimeUtils.toLocalDate(value)
            .toEpochDay()),
    DATETIME(CustomType.DATETIME.getName(), true, value -> DateTimeUtils.toLocalDateTime(value)
            .toEpochSecond(ZoneOffset.UTC)),
    TIME(CustomType.TIME.getName(), true, value -> DateTimeUtils.toLocalTime(value)
            .toSecondOfDay()),
    ZONED_DATETIME(CustomType.ZONED_DATETIME.getName(), true, value -> ZonedDateTimeUtils.parse(value)
            .toEpochSecond());

    private final String type;

    /**
     * Соседние значения типа отстоят друг от друга ровно на единицу шкалы: целые числа, дни, секунды
     */
    private final boolean unitStep;

    private final ToDoubleFunction<String> parser;

    /**
     * @return положение значения на шкале
     * @throws RuntimeException если значение записано не в формате типа
     */
    public double position(String value) {
        return parser.applyAsDouble(value);
    }

    /**
     * У типа нет отдельных значений, которые можно пронумеровать
     */
    public boolean isContinuous() {
        return this == DOUBLE;
    }

    /**
     * @param type имя базового или пользовательского типа колонки
     * @return null, если для типа распределения не поддерживаются
     */
    @Nullable
    public static DistributionAxis of(String type) {
        for (DistributionAxis axis : values()) {
            if (axis.getType().equals(type)) {
                return axis;
            }
        }

        return null;
    }

    public static List<String> types() {
        return Arrays.stream(values()).map(DistributionAxis::getType).toList();
    }
}
