package ru.akvine.istochnik.services.distribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.compozit.commons.istochnik.DistributionDto;
import ru.akvine.istochnik.exceptions.InvalidDistributionException;

@DisplayName("Distribution factory tests")
public class DistributionFactoryTest {
    private static final int SAMPLES = 200_000;
    private static final long SEED = 42;

    @Test
    @DisplayName("Absent distribution and uniform type give the uniform distribution")
    void uniform_distribution_is_default() {
        assertThat(DistributionFactory.create(null, DistributionAxis.INTEGER, "0", "10"))
                .isSameAs(UniformDistribution.INSTANCE);
        assertThat(DistributionFactory.create(distribution("uniform"), DistributionAxis.INTEGER, "0", "10"))
                .isSameAs(UniformDistribution.INSTANCE);
        // Без распределения тип колонки и границы не проверяются: они могут относиться к другой стратегии
        assertThat(DistributionFactory.create(null, null, null, null)).isSameAs(UniformDistribution.INSTANCE);
    }

    @Test
    @DisplayName("Normal distribution - mean and stddev are given in units of the column type")
    void normal_distribution_in_type_units() {
        Distribution distribution = DistributionFactory.create(
                distribution("normal").setMean("50").setStddev("10"), DistributionAxis.INTEGER, "0", "101");

        long[] values = longs(distribution, 0, 101);
        assertInRange(values, 0, 101);
        // Среднее приходится на само значение 50, а не на границу между 49 и 50
        assertThat(mean(values)).isCloseTo(50, within(0.15));
        assertThat(share(values, 40, 60)).isCloseTo(0.706, within(0.01));
    }

    @Test
    @DisplayName("Normal distribution without parameters - mean in the middle, six stddev in the range")
    void normal_distribution_defaults() {
        Distribution distribution =
                DistributionFactory.create(distribution("normal"), DistributionAxis.DOUBLE, "-30", "30");

        Random random = new Random(SEED);
        double[] values = new double[SAMPLES];
        for (int i = 0; i < SAMPLES; i++) {
            values[i] = distribution.nextDouble(-30, 30, random);
        }

        assertThat(java.util.Arrays.stream(values).min().orElseThrow()).isGreaterThanOrEqualTo(-30);
        assertThat(java.util.Arrays.stream(values).max().orElseThrow()).isLessThan(30);
        double mean = java.util.Arrays.stream(values).average().orElseThrow();
        assertThat(mean).isCloseTo(0, within(0.15));
        double within = (double) java.util.Arrays.stream(values)
                        .filter(value -> Math.abs(value) < 10)
                        .count()
                / SAMPLES;
        assertThat(within).isCloseTo(0.683, within(0.01));
    }

    @Test
    @DisplayName("Normal distribution for dates - mean is a date, stddev is in days")
    void normal_distribution_for_dates() {
        Distribution distribution = DistributionFactory.create(
                distribution("normal").setMean("2025-07-01").setStddev("30"),
                DistributionAxis.DATE,
                "2025-01-01",
                "2026-01-01");

        long start = LocalDate.parse("2025-01-01").toEpochDay();
        long end = LocalDate.parse("2026-01-01").toEpochDay();
        long center = LocalDate.parse("2025-07-01").toEpochDay();
        long[] values = longs(distribution, start, end);

        assertInRange(values, start, end);
        assertThat(mean(values)).isCloseTo(center, within(0.5));
        assertThat(share(values, center - 30, center + 30)).isCloseTo(0.69, within(0.01));
    }

    @Test
    @DisplayName("Exponential distribution - stddev is the mean distance from the peak in units of the column type")
    void exponential_distribution_in_type_units() {
        Distribution fromStart = DistributionFactory.create(
                distribution("exponential").setStddev("50"), DistributionAxis.INTEGER, "0", "1000");
        Distribution fromEnd = DistributionFactory.create(
                distribution("exponential").setStddev("50").setPeak("end"), DistributionAxis.INTEGER, "0", "1000");

        // Целые значения получаются отбрасыванием дробной части, поэтому среднее на полшага меньше
        assertThat(mean(longs(fromStart, 0, 1000))).isCloseTo(49.5, within(0.6));
        assertThat(mean(longs(fromEnd, 0, 1000))).isCloseTo(949.5, within(0.6));
    }

    @Test
    @DisplayName("Exponential distribution without parameters - mean distance is a fifth of the range")
    void exponential_distribution_defaults() {
        Distribution distribution =
                DistributionFactory.create(distribution("exponential"), DistributionAxis.TIME, "00:00:00", "10:00:00");

        long[] values = longs(distribution, 0, 36_000);
        assertInRange(values, 0, 36_000);
        assertThat(share(values, 0, 7_200)).isCloseTo((1 - Math.exp(-1)) / (1 - Math.exp(-5)), within(0.01));
    }

    @Test
    @DisplayName("Zipf distribution - exponent and peak are applied")
    void zipf_distribution() {
        Distribution fromStart =
                DistributionFactory.create(distribution("zipf"), DistributionAxis.INTEGER, "1", "1001");
        Distribution fromEnd = DistributionFactory.create(
                distribution("ZIPF").setExponent("2").setPeak("END"), DistributionAxis.DECIMAL, "0.00", "10.00");

        long[] values = longs(fromStart, 1, 1001);
        assertThat((double) count(values, 1) / count(values, 2)).isCloseTo(2, within(0.1));

        long[] mirrored = longs(fromEnd, 0, 1000);
        assertThat((double) count(mirrored, 999) / count(mirrored, 998)).isCloseTo(4, within(0.2));
    }

    @Test
    @DisplayName("Each supported type has an axis that parses values in the format of the type")
    void axes() {
        assertThat(DistributionAxis.types())
                .containsExactly("int", "double", "decimal", "date", "datetime", "time", "zoned_datetime");
        assertThat(DistributionAxis.of("str")).isNull();
        assertThat(DistributionAxis.of("snils")).isNull();

        assertThat(DistributionAxis.of("int").position(" 42 ")).isEqualTo(42);
        assertThat(DistributionAxis.of("double").position("1.5")).isEqualTo(1.5);
        assertThat(DistributionAxis.of("decimal").position("12.50")).isEqualTo(12.5);
        assertThat(DistributionAxis.of("date").position("1970-01-11")).isEqualTo(10);
        assertThat(DistributionAxis.of("datetime").position("1970-01-01 00:01:00"))
                .isEqualTo(60);
        assertThat(DistributionAxis.of("time").position("01:00:00")).isEqualTo(3600);
        assertThat(DistributionAxis.of("zoned_datetime").position("1970-01-01T03:00:10+03:00[Europe/Moscow]"))
                .isEqualTo(10);

        assertThat(DistributionAxis.DOUBLE.isContinuous()).isTrue();
        assertThat(DistributionAxis.DECIMAL.isContinuous()).isFalse();
        assertThat(DistributionAxis.INTEGER.isUnitStep()).isTrue();
        assertThat(DistributionAxis.DECIMAL.isUnitStep()).isFalse();
    }

    @Test
    @DisplayName("Unknown type of distribution and unsupported column type are rejected")
    void invalid_type_is_rejected() {
        assertInvalid(
                distribution("gauss"), DistributionAxis.INTEGER, "0", "10", "field [distribution.type] must be one of");
        assertInvalid(
                distribution(null), DistributionAxis.INTEGER, "0", "10", "field [distribution.type] must be one of");
        assertInvalid(distribution("normal"), null, "0", "10", "field [distribution] is supported only for types");
        assertInvalid(
                distribution("zipf"),
                DistributionAxis.DOUBLE,
                "0",
                "10",
                "[zipf] distribution is not supported for type [double]");
    }

    @Test
    @DisplayName("Parameters of another distribution are rejected")
    void foreign_parameters_are_rejected() {
        assertInvalid(
                distribution("uniform").setMean("5"),
                DistributionAxis.INTEGER,
                "0",
                "10",
                "field [distribution.mean] is not used by [uniform] distribution. Supported fields: []");
        assertInvalid(
                distribution("exponential").setMean("5"),
                DistributionAxis.INTEGER,
                "0",
                "10",
                "field [distribution.mean] is not used by [exponential] distribution. Supported fields: [peak, stddev]");
        assertInvalid(
                distribution("normal").setPeak("end").setExponent("2"),
                DistributionAxis.INTEGER,
                "0",
                "10",
                "field [distribution.exponent] is not used by [normal] distribution. Supported fields: [mean, stddev]",
                "field [distribution.peak] is not used by [normal] distribution");
        assertInvalid(
                distribution("zipf").setStddev("2"),
                DistributionAxis.INTEGER,
                "0",
                "10",
                "field [distribution.stddev] is not used by [zipf] distribution. Supported fields: [exponent, peak]");
    }

    @Test
    @DisplayName("Invalid values of parameters are rejected")
    void invalid_parameters_are_rejected() {
        String meanFormat = "field [distribution.mean] must have the same format as field [start]";
        String meanRange = "field [distribution.mean] must be between field [start] and field [end]";
        String stddev = "field [distribution.stddev] must be a number greater than 0";
        String exponent = "field [distribution.exponent] must be a number greater than 0 and not greater than 10";
        String peak = "field [distribution.peak] must be one of [start, end]";

        assertInvalid(distribution("normal").setMean("abc"), DistributionAxis.INTEGER, "0", "10", meanFormat);
        assertInvalid(
                distribution("normal").setMean("5"), DistributionAxis.DATE, "2025-01-01", "2025-02-01", meanFormat);
        assertInvalid(distribution("normal").setMean("-1"), DistributionAxis.INTEGER, "0", "10", meanRange);
        assertInvalid(distribution("normal").setMean("10.5"), DistributionAxis.DOUBLE, "0", "10", meanRange);
        assertInvalid(
                distribution("normal").setMean("2025-03-01"),
                DistributionAxis.DATE,
                "2025-01-01",
                "2025-02-01",
                meanRange);

        for (String value : new String[] {"0", "-5", "abc", "NaN", "Infinity", "9".repeat(200)}) {
            assertInvalid(distribution("normal").setStddev(value), DistributionAxis.INTEGER, "0", "10", stddev);
            assertInvalid(distribution("exponential").setStddev(value), DistributionAxis.INTEGER, "0", "10", stddev);
        }
        for (String value : new String[] {"0", "-1", "10.5", "abc", "NaN"}) {
            assertInvalid(distribution("zipf").setExponent(value), DistributionAxis.INTEGER, "0", "10", exponent);
        }
        assertInvalid(distribution("zipf").setPeak("middle"), DistributionAxis.INTEGER, "0", "10", peak);
        assertInvalid(distribution("exponential").setPeak("top"), DistributionAxis.INTEGER, "0", "10", peak);

        // Несколько ошибок возвращаются вместе
        assertInvalid(
                distribution("normal").setMean("abc").setStddev("-1"),
                DistributionAxis.INTEGER,
                "0",
                "10",
                meanFormat,
                stddev);
    }

    @Test
    @DisplayName("Distribution needs a valid non-empty range")
    void invalid_range_is_rejected() {
        String range = "field [distribution] requires valid fields [start] and [end]";
        String order = "field [end] must be greater than field [start] to apply distribution";

        assertInvalid(distribution("normal"), DistributionAxis.INTEGER, "abc", "10", range);
        assertInvalid(distribution("normal"), DistributionAxis.INTEGER, "0", null, range);
        assertInvalid(distribution("zipf"), DistributionAxis.DATE, "2025-01-01", "tomorrow", range);
        assertInvalid(distribution("normal"), DistributionAxis.DECIMAL, "0", "1e999999999", range);
        assertInvalid(distribution("normal"), DistributionAxis.INTEGER, "10", "10", order);
        assertInvalid(distribution("exponential"), DistributionAxis.DOUBLE, "10", "5", order);
    }

    private void assertInvalid(
            DistributionDto distribution, DistributionAxis axis, String start, String end, String... expectedErrors) {
        assertThatThrownBy(() -> DistributionFactory.create(distribution, axis, start, end))
                .isInstanceOfSatisfying(InvalidDistributionException.class, exception -> {
                    assertThat(exception.getErrors()).hasSameSizeAs(expectedErrors);
                    for (String expectedError : expectedErrors) {
                        assertThat(exception.getErrors()).anyMatch(error -> error.contains(expectedError));
                    }
                });
    }

    private DistributionDto distribution(String type) {
        return new DistributionDto().setType(type);
    }

    private long[] longs(Distribution distribution, long start, long end) {
        Random random = new Random(SEED);
        return LongStream.generate(() -> distribution.nextLong(start, end, random))
                .limit(SAMPLES)
                .toArray();
    }

    private void assertInRange(long[] values, long start, long end) {
        assertThat(LongStream.of(values).min().orElseThrow()).isGreaterThanOrEqualTo(start);
        assertThat(LongStream.of(values).max().orElseThrow()).isLessThan(end);
    }

    private double mean(long[] values) {
        return LongStream.of(values).average().orElseThrow();
    }

    private double share(long[] values, long from, long to) {
        return (double) LongStream.of(values)
                        .filter(value -> value >= from && value <= to)
                        .count()
                / values.length;
    }

    private long count(long[] values, long expected) {
        return LongStream.of(values).filter(value -> value == expected).count();
    }
}
