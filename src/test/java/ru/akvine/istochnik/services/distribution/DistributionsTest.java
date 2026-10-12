package ru.akvine.istochnik.services.distribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Distributions tests")
public class DistributionsTest {
    private static final int SAMPLES = 200_000;
    private static final long SEED = 42;

    @Test
    @DisplayName("Uniform distribution requests values from generator exactly as generators did before distributions")
    void uniform_keeps_seeded_values() {
        Random actual = new Random(SEED);
        Random expected = new Random(SEED);

        for (int i = 0; i < 100; i++) {
            assertThat(UniformDistribution.INSTANCE.nextLong(-5, 500, actual)).isEqualTo(expected.nextLong(-5, 500));
            assertThat(UniformDistribution.INSTANCE.nextInt(10, 86_400, actual))
                    .isEqualTo(expected.nextInt(10, 86_400));
            assertThat(UniformDistribution.INSTANCE.nextDouble(-1.5, 2.5, actual))
                    .isEqualTo(expected.nextDouble(-1.5, 2.5));
        }

        BigInteger bound = BigInteger.TEN.pow(30);
        assertThat(UniformDistribution.INSTANCE.nextOffset(bound, new Random(SEED)))
                .isBetween(BigInteger.ZERO, bound.subtract(BigInteger.ONE))
                .isEqualTo(UniformDistribution.INSTANCE.nextOffset(bound, new Random(SEED)));
    }

    @Test
    @DisplayName("Normal distribution - values are gathered around the mean inside the range")
    void normal_distribution() {
        double[] values = doubles(new NormalDistribution(0.5, 0.1), 0, 100);

        assertInRange(values, 0, 100);
        assertThat(mean(values)).isCloseTo(50, within(0.2));
        assertThat(stddev(values)).isCloseTo(10, within(0.2));
        // В пределах одного стандартного отклонения от среднего лежит 68,3 % значений, в пределах двух - 95,4 %
        assertThat(share(values, 40, 60)).isCloseTo(0.683, within(0.01));
        assertThat(share(values, 30, 70)).isCloseTo(0.954, within(0.01));
    }

    @Test
    @DisplayName("Normal distribution with the mean at the border - the part outside the range is cut off")
    void normal_distribution_is_truncated() {
        double[] values = doubles(new NormalDistribution(0, 0.1), 0, 100);

        assertInRange(values, 0, 100);
        // Среднее половины нормального распределения: stddev * sqrt(2 / pi)
        assertThat(mean(values)).isCloseTo(10 * Math.sqrt(2 / Math.PI), within(0.2));
    }

    @Test
    @DisplayName("Normal distribution much wider than the range is almost uniform")
    void wide_normal_distribution() {
        double[] values = doubles(new NormalDistribution(0.5, 5), 0, 100);

        assertInRange(values, 0, 100);
        assertThat(mean(values)).isCloseTo(50, within(0.5));
        assertThat(share(values, 0, 25)).isCloseTo(0.25, within(0.01));
        assertThat(share(values, 75, 100)).isCloseTo(0.25, within(0.01));
    }

    @Test
    @DisplayName("Exponential distribution - values are dense at the start and become rarer towards the end")
    void exponential_distribution() {
        double[] values = doubles(new ExponentialDistribution(0.1, false), 0, 100);

        assertInRange(values, 0, 100);
        assertThat(mean(values)).isCloseTo(10, within(0.2));
        assertThat(stddev(values)).isCloseTo(10, within(0.3));
        // Доля значений не дальше среднего расстояния от пика: 1 - 1 / e
        assertThat(share(values, 0, 10)).isCloseTo(1 - Math.exp(-1), within(0.01));
        assertThat(share(values, 0, 20)).isGreaterThan(share(values, 20, 40));
    }

    @Test
    @DisplayName("Exponential distribution with the peak at the end is a mirror of the one with the peak at the start")
    void exponential_distribution_with_peak_at_end() {
        double[] values = doubles(new ExponentialDistribution(0.1, true), 0, 100);

        assertInRange(values, 0, 100);
        assertThat(mean(values)).isCloseTo(90, within(0.2));
        assertThat(share(values, 90, 100)).isCloseTo(1 - Math.exp(-1), within(0.01));
    }

    @Test
    @DisplayName("Exponential distribution wider than the range is cut off by the range")
    void exponential_distribution_is_truncated() {
        double[] values = doubles(new ExponentialDistribution(1, false), 0, 100);

        assertInRange(values, 0, 100);
        // Среднее распределения, усечённого на расстоянии scale от пика: scale * (1 - 1 / (e - 1))
        assertThat(mean(values)).isCloseTo(100 * (1 - 1 / (Math.E - 1)), within(0.3));
    }

    @Test
    @DisplayName("Fraction distributions produce integer and big integer values inside the range")
    void fraction_distributions_produce_discrete_values() {
        Random random = new Random(SEED);
        Distribution distribution = new NormalDistribution(0.5, 0.2);
        BigInteger bound = BigInteger.TEN.pow(30);

        for (int i = 0; i < 10_000; i++) {
            assertThat(distribution.nextLong(-3, 4, random)).isBetween(-3L, 3L);
            assertThat(distribution.nextInt(0, 2, random)).isBetween(0, 1);
            assertThat(distribution.nextOffset(bound, random))
                    .isBetween(BigInteger.ZERO, bound.subtract(BigInteger.ONE));
            assertThat(distribution.nextOffset(BigInteger.ONE, random)).isEqualTo(BigInteger.ZERO);
        }
    }

    @Test
    @DisplayName("Zipf distribution - value with rank k has probability proportional to 1 / k^exponent")
    void zipf_distribution() {
        verifyZipfFrequencies(1, 10);
        verifyZipfFrequencies(2, 10);
        verifyZipfFrequencies(0.5, 25);
        verifyZipfFrequencies(1.0000000001, 10);
    }

    @Test
    @DisplayName("Zipf distribution with the peak at the end - the most frequent value is the last one")
    void zipf_distribution_with_peak_at_end() {
        Map<Long, Integer> counts = counts(new ZipfDistribution(1, true), 100, 110);

        assertThat(counts.keySet()).allMatch(value -> value >= 100 && value < 110);
        assertThat(counts.get(109L)).isGreaterThan(counts.get(108L));
        assertThat(counts.get(108L)).isGreaterThan(counts.get(100L));
        assertThat((double) counts.get(109L) / counts.get(108L)).isCloseTo(2, within(0.1));
    }

    @Test
    @DisplayName("Zipf distribution over a huge range - time of a draw doesn't depend on the range size")
    void zipf_distribution_over_huge_range() {
        long start = 1;
        long end = 1_000_000_000_001L;
        Map<Long, Integer> counts = counts(new ZipfDistribution(1, false), start, end);

        assertThat(counts.keySet()).allMatch(value -> value >= start && value < end);
        // Доля первого значения: 1 / H(n), где H(n) - гармоническое число, примерно ln(n) + 0,5772
        double expectedShare = 1 / (Math.log(end - start) + 0.5772);
        assertThat((double) counts.get(start) / SAMPLES).isCloseTo(expectedShare, within(0.003));
        // Длинный хвост: встречаются и значения из дальней половины диапазона
        assertThat(counts.keySet()).anyMatch(value -> value > end / 2);
    }

    @Test
    @DisplayName("Zipf distribution over a single value, a big integer range and a continuous range")
    void zipf_distribution_edge_cases() {
        Random random = new Random(SEED);
        Distribution distribution = new ZipfDistribution(1.5, true);
        BigInteger bound = BigInteger.TEN.pow(30);

        for (int i = 0; i < 1000; i++) {
            assertThat(distribution.nextLong(7, 8, random)).isEqualTo(7);
            assertThat(distribution.nextOffset(bound, random))
                    .isBetween(BigInteger.ZERO, bound.subtract(BigInteger.ONE));
        }
        // Пик у конца диапазона: чаще всего выбирается последнее значение
        assertThat(distribution.nextOffset(bound, new Random(SEED)).bitLength()).isGreaterThan(Long.SIZE);

        assertThatThrownBy(() -> distribution.nextDouble(0, 1, random))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Distributions reject parameters with which a draw could not be finished")
    void invalid_parameters_are_rejected() {
        assertThatThrownBy(() -> new NormalDistribution(-0.1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NormalDistribution(1.1, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NormalDistribution(Double.NaN, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NormalDistribution(0.5, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NormalDistribution(0.5, Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ExponentialDistribution(0, false)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ExponentialDistribution(Double.NaN, false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ZipfDistribution(0, false)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ZipfDistribution(Double.NaN, false)).isInstanceOf(IllegalArgumentException.class);
    }

    private void verifyZipfFrequencies(double exponent, int valuesCount) {
        Map<Long, Integer> counts = counts(new ZipfDistribution(exponent, false), 1, valuesCount + 1L);

        double normalization = LongStream.rangeClosed(1, valuesCount)
                .mapToDouble(rank -> Math.pow(rank, -exponent))
                .sum();
        assertThat(counts.keySet()).allMatch(value -> value >= 1 && value <= valuesCount);
        for (long rank = 1; rank <= valuesCount; rank++) {
            double expectedShare = Math.pow(rank, -exponent) / normalization;
            assertThat((double) counts.getOrDefault(rank, 0) / SAMPLES)
                    .as("share of rank %s for exponent %s", rank, exponent)
                    .isCloseTo(expectedShare, within(0.005));
        }
    }

    private double[] doubles(Distribution distribution, double start, double end) {
        Random random = new Random(SEED);
        return DoubleStream.generate(() -> distribution.nextDouble(start, end, random))
                .limit(SAMPLES)
                .toArray();
    }

    private Map<Long, Integer> counts(Distribution distribution, long start, long end) {
        Random random = new Random(SEED);
        Map<Long, Integer> counts = new HashMap<>();
        for (int i = 0; i < SAMPLES; i++) {
            counts.merge(distribution.nextLong(start, end, random), 1, Integer::sum);
        }

        return counts;
    }

    private void assertInRange(double[] values, double start, double end) {
        assertThat(DoubleStream.of(values).min().orElseThrow()).isGreaterThanOrEqualTo(start);
        assertThat(DoubleStream.of(values).max().orElseThrow()).isLessThan(end);
    }

    private double mean(double[] values) {
        return DoubleStream.of(values).average().orElseThrow();
    }

    private double stddev(double[] values) {
        double mean = mean(values);
        return Math.sqrt(DoubleStream.of(values)
                .map(value -> (value - mean) * (value - mean))
                .average()
                .orElseThrow());
    }

    private double share(double[] values, double from, double to) {
        return (double) DoubleStream.of(values)
                        .filter(value -> value >= from && value < to)
                        .count()
                / values.length;
    }
}
