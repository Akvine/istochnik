package ru.akvine.istochnik.services.distribution;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import ru.akvine.istochnik.controllers.dto.DistributionDto;
import ru.akvine.istochnik.enums.DistributionPeak;
import ru.akvine.istochnik.enums.DistributionType;
import ru.akvine.istochnik.exceptions.InvalidDistributionException;

/**
 * Строит распределение по его описанию из запроса. Параметры заданы в единицах типа колонки,
 * а распределения работают с долями ширины диапазона, поэтому здесь же параметры пересчитываются в доли
 */
@UtilityClass
public class DistributionFactory {
    // Значения по умолчанию - в долях ширины диапазона. При таком отклонении нормального распределения
    // в диапазон без усечения попадает 99,7 % значений
    private static final double DEFAULT_NORMAL_STDDEV = 1.0 / 6;
    private static final double DEFAULT_EXPONENTIAL_STDDEV = 1.0 / 5;
    private static final double DEFAULT_ZIPF_EXPONENT = 1;
    // При большем показателе практически все значения совпадают с пиком
    private static final double MAX_ZIPF_EXPONENT = 10;
    // Разбор очень длинной записи числа занимает заметное время, а осмысленные значения намного короче
    private static final int MAX_VALUE_LENGTH = 100;

    private static final String MEAN = "mean";
    private static final String STDDEV = "stddev";
    private static final String EXPONENT = "exponent";
    private static final String PEAK = "peak";

    private static final Map<DistributionType, Set<String>> SUPPORTED_PARAMETERS = Map.of(
            DistributionType.UNIFORM, Set.of(),
            DistributionType.NORMAL, Set.of(MEAN, STDDEV),
            DistributionType.EXPONENTIAL, Set.of(STDDEV, PEAK),
            DistributionType.ZIPF, Set.of(EXPONENT, PEAK));

    /**
     * @param distribution описание распределения из запроса; null означает равномерное распределение
     * @param axis         шкала типа колонки; null, если для типа распределения не поддерживаются
     * @param start        начало диапазона в формате типа колонки
     * @param end          конец диапазона в формате типа колонки
     * @throws InvalidDistributionException если распределение описано с ошибками
     */
    public Distribution create(
            @Nullable DistributionDto distribution, @Nullable DistributionAxis axis, String start, String end) {
        if (distribution == null) {
            return UniformDistribution.INSTANCE;
        }

        List<String> errors = new ArrayList<>();
        if (axis == null) {
            errors.add("field [distribution] is supported only for types " + DistributionAxis.types());
        }

        DistributionType type = DistributionType.from(distribution.getType());
        if (type == null) {
            errors.add("field [distribution.type] must be one of " + DistributionType.names());
        }

        if (!errors.isEmpty()) {
            throw new InvalidDistributionException(errors);
        }

        verifyParameters(distribution, type, errors);
        Distribution result = type == DistributionType.UNIFORM
                ? UniformDistribution.INSTANCE
                : createShaped(distribution, type, axis, start, end, errors);

        if (!errors.isEmpty()) {
            throw new InvalidDistributionException(errors);
        }
        return result;
    }

    // Параметр другого распределения молча не отбрасывается: чаще всего это ошибка в запросе
    private void verifyParameters(DistributionDto distribution, DistributionType type, List<String> errors) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put(MEAN, distribution.getMean());
        parameters.put(STDDEV, distribution.getStddev());
        parameters.put(EXPONENT, distribution.getExponent());
        parameters.put(PEAK, distribution.getPeak());

        Set<String> supported = SUPPORTED_PARAMETERS.get(type);
        parameters.forEach((name, value) -> {
            if (StringUtils.isNotBlank(value) && !supported.contains(name)) {
                errors.add(String.format(
                        "field [distribution.%s] is not used by [%s] distribution. Supported fields: %s",
                        name, type.getName(), supported.stream().sorted().toList()));
            }
        });
    }

    @Nullable
    private Distribution createShaped(
            DistributionDto distribution,
            DistributionType type,
            DistributionAxis axis,
            String start,
            String end,
            List<String> errors) {
        Double startPosition = position(axis, start);
        Double endPosition = position(axis, end);
        if (startPosition == null || endPosition == null) {
            errors.add("field [distribution] requires valid fields [start] and [end]");
            return null;
        }

        double width = endPosition - startPosition;
        if (!(width > 0)) {
            errors.add("field [end] must be greater than field [start] to apply distribution");
            return null;
        }

        return switch (type) {
            case NORMAL -> createNormal(distribution, axis, startPosition, width, errors);
            case EXPONENTIAL -> createExponential(distribution, width, errors);
            case ZIPF -> createZipf(distribution, axis, errors);
            case UNIFORM -> UniformDistribution.INSTANCE;
        };
    }

    @Nullable
    private Distribution createNormal(
            DistributionDto distribution, DistributionAxis axis, double start, double width, List<String> errors) {
        Double stddev = parseStddev(distribution, errors);

        // По умолчанию среднее - середина диапазона
        double mean = 0.5;
        if (StringUtils.isNotBlank(distribution.getMean())) {
            Double position = position(axis, distribution.getMean());
            if (position == null) {
                errors.add(String.format("field [distribution.%s] must have the same format as field [start]", MEAN));
            } else if (position < start || position > start + width) {
                errors.add(
                        String.format("field [distribution.%s] must be between field [start] and field [end]", MEAN));
            } else {
                // Отдельное значение занимает на шкале отрезок единичной длины, и само значение - его середина
                double center = axis.isUnitStep() ? position + 0.5 : position;
                mean = Math.min((center - start) / width, 1);
            }
        }

        if (!errors.isEmpty()) {
            return null;
        }
        return new NormalDistribution(mean, stddev == null ? DEFAULT_NORMAL_STDDEV : stddev / width);
    }

    @Nullable
    private Distribution createExponential(DistributionDto distribution, double width, List<String> errors) {
        Double stddev = parseStddev(distribution, errors);
        boolean peakAtEnd = parsePeakAtEnd(distribution, errors);

        if (!errors.isEmpty()) {
            return null;
        }
        return new ExponentialDistribution(stddev == null ? DEFAULT_EXPONENTIAL_STDDEV : stddev / width, peakAtEnd);
    }

    @Nullable
    private Distribution createZipf(DistributionDto distribution, DistributionAxis axis, List<String> errors) {
        if (axis.isContinuous()) {
            errors.add(String.format(
                    "[%s] distribution is not supported for type [%s]: it is defined only for discrete values",
                    DistributionType.ZIPF.getName(), axis.getType()));
        }

        double exponent = DEFAULT_ZIPF_EXPONENT;
        if (StringUtils.isNotBlank(distribution.getExponent())) {
            Double parsed = parseNumber(distribution.getExponent());
            if (parsed == null || parsed <= 0 || parsed > MAX_ZIPF_EXPONENT) {
                errors.add(String.format(
                        "field [distribution.%s] must be a number greater than 0 and not greater than %s",
                        EXPONENT, (int) MAX_ZIPF_EXPONENT));
            } else {
                exponent = parsed;
            }
        }
        boolean peakAtEnd = parsePeakAtEnd(distribution, errors);

        if (!errors.isEmpty()) {
            return null;
        }
        return new ZipfDistribution(exponent, peakAtEnd);
    }

    /**
     * @return стандартное отклонение в единицах шкалы; null, если оно не задано или задано с ошибкой
     */
    @Nullable
    private Double parseStddev(DistributionDto distribution, List<String> errors) {
        if (StringUtils.isBlank(distribution.getStddev())) {
            return null;
        }

        Double stddev = parseNumber(distribution.getStddev());
        if (stddev == null || stddev <= 0) {
            errors.add(String.format("field [distribution.%s] must be a number greater than 0", STDDEV));
            return null;
        }
        return stddev;
    }

    private boolean parsePeakAtEnd(DistributionDto distribution, List<String> errors) {
        if (StringUtils.isBlank(distribution.getPeak())) {
            return false;
        }

        DistributionPeak peak = DistributionPeak.from(distribution.getPeak().trim());
        if (peak == null) {
            errors.add(String.format("field [distribution.%s] must be one of %s", PEAK, DistributionPeak.names()));
        }
        return peak == DistributionPeak.END;
    }

    @Nullable
    private Double parseNumber(String value) {
        if (value.length() > MAX_VALUE_LENGTH) {
            return null;
        }

        try {
            double number = Double.parseDouble(value.trim());
            return Double.isFinite(number) ? number : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Nullable
    private Double position(DistributionAxis axis, @Nullable String value) {
        if (StringUtils.isBlank(value) || value.length() > MAX_VALUE_LENGTH) {
            return null;
        }

        try {
            double position = axis.position(value);
            return Double.isFinite(position) ? position : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
