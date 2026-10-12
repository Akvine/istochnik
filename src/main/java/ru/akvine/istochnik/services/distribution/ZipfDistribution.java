package ru.akvine.istochnik.services.distribution;

import java.math.BigInteger;
import java.util.random.RandomGenerator;
import ru.akvine.istochnik.exceptions.GenerationException;

/**
 * Распределение Ципфа: значения диапазона упорядочены по убыванию частоты, и значение с номером k встречается
 * с вероятностью, пропорциональной 1 / k^exponent. Немногие значения у пика встречаются очень часто,
 * остальные образуют длинный хвост. Определено только для типов с отдельными значениями: целых чисел, дат, времени
 */
public final class ZipfDistribution implements Distribution {
    private final double exponent;
    private final boolean peakAtEnd;

    // Вспомогательные величины зависят от количества значений в диапазоне, а оно у колонки одно и то же
    private RankSampler sampler;

    /**
     * @param exponent  показатель степени: чем он больше, тем сильнее преобладают самые частые значения
     * @param peakAtEnd самое частое значение - последнее в диапазоне, а не первое
     */
    public ZipfDistribution(double exponent, boolean peakAtEnd) {
        if (!(exponent > 0 && Double.isFinite(exponent))) {
            throw new IllegalArgumentException("exponent = [" + exponent + "] must be greater than 0");
        }

        this.exponent = exponent;
        this.peakAtEnd = peakAtEnd;
    }

    @Override
    public long nextLong(long start, long end, RandomGenerator randomGenerator) {
        long offset = nextRank(Math.subtractExact(end, start), randomGenerator) - 1;
        return peakAtEnd ? end - 1 - offset : start + offset;
    }

    @Override
    public double nextDouble(double start, double end, RandomGenerator randomGenerator) {
        throw new UnsupportedOperationException("Zipf distribution is defined only for discrete values");
    }

    @Override
    public BigInteger nextOffset(BigInteger bound, RandomGenerator randomGenerator) {
        // Вероятность номеров за пределами long пренебрежимо мала, поэтому хвост отбрасывается
        long count = bound.bitLength() < Long.SIZE ? bound.longValue() : Long.MAX_VALUE;
        BigInteger offset = BigInteger.valueOf(nextRank(count, randomGenerator) - 1);
        return peakAtEnd ? bound.subtract(BigInteger.ONE).subtract(offset) : offset;
    }

    private long nextRank(long count, RandomGenerator randomGenerator) {
        RankSampler current = sampler;
        if (current == null || current.count != count) {
            current = new RankSampler(count, exponent);
            sampler = current;
        }

        return current.sample(randomGenerator);
    }

    /**
     * Выбор номера значения методом отбора с обращением (W. Hörmann, G. Derflinger, "Rejection-inversion
     * to generate variates from monotone discrete distributions", 1996). Время выбора не зависит от количества
     * значений, поэтому метод годится и для диапазонов из миллиардов значений
     */
    private static final class RankSampler {
        // Ближе к нулю отношения log1p(t) / t и expm1(t) / t считаются по ряду Тейлора, иначе теряется точность
        private static final double TAYLOR_THRESHOLD = 1e-8;
        // Принимается подавляющее большинство кандидатов. Ограничение нужно только как защита от зацикливания
        private static final int MAX_ATTEMPTS = 1000;

        private final long count;
        private final double exponent;
        private final double integralAtFirst;
        private final double integralAtLast;
        private final double acceptanceThreshold;

        private RankSampler(long count, double exponent) {
            this.count = count;
            this.exponent = exponent;
            this.integralAtFirst = integral(1.5) - 1;
            this.integralAtLast = integral(count + 0.5);
            this.acceptanceThreshold = 2 - integralInverse(integral(2.5) - density(2));
        }

        private long sample(RandomGenerator randomGenerator) {
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                double point = integralAtLast + randomGenerator.nextDouble() * (integralAtFirst - integralAtLast);
                double position = integralInverse(point);
                long rank = Math.min(Math.max((long) (position + 0.5), 1), count);

                if (rank - position <= acceptanceThreshold || point >= integral(rank + 0.5) - density(rank)) {
                    return rank;
                }
            }

            throw new GenerationException("Can't generate value with Zipf distribution");
        }

        // Плотность, которой накрываются вероятности номеров: 1 / x^exponent
        private double density(double x) {
            return Math.exp(-exponent * Math.log(x));
        }

        // Интеграл плотности: (x^(1 - exponent) - 1) / (1 - exponent), а при exponent = 1 - ln(x)
        private double integral(double x) {
            double logarithm = Math.log(x);
            return expm1Ratio((1 - exponent) * logarithm) * logarithm;
        }

        private double integralInverse(double value) {
            // Из-за погрешности вычислений аргумент логарифма может оказаться чуть меньше нуля
            double argument = Math.max(value * (1 - exponent), -1);
            return Math.exp(log1pRatio(argument) * value);
        }

        private static double log1pRatio(double t) {
            if (Math.abs(t) > TAYLOR_THRESHOLD) {
                return Math.log1p(t) / t;
            }
            return 1 - t * (1.0 / 2 - t * (1.0 / 3 - t / 4));
        }

        private static double expm1Ratio(double t) {
            if (Math.abs(t) > TAYLOR_THRESHOLD) {
                return Math.expm1(t) / t;
            }
            return 1 + t / 2 * (1 + t / 3 * (1 + t / 4));
        }
    }
}
