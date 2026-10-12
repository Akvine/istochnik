package ru.akvine.istochnik.services.distribution;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.random.RandomGenerator;

/**
 * Распределение, заданное на непрерывной шкале. Наследник выбирает положение значения внутри диапазона
 * как долю его ширины, а этот класс переводит долю в значение нужного типа
 */
abstract class AbstractFractionDistribution implements Distribution {
    /**
     * Наибольшая допустимая доля: правая граница в диапазон не входит
     */
    protected static final double MAX_FRACTION = Math.nextDown(1.0);

    /**
     * @return положение значения в диапазоне: 0 - начало диапазона, 1 - его конец; единица не возвращается
     */
    protected abstract double nextFraction(RandomGenerator randomGenerator);

    @Override
    public long nextLong(long start, long end, RandomGenerator randomGenerator) {
        long count = Math.subtractExact(end, start);
        // На очень широком диапазоне произведение из-за округления может дотянуться до правой границы
        return start + Math.min((long) (nextFraction(randomGenerator) * count), count - 1);
    }

    @Override
    public double nextDouble(double start, double end, RandomGenerator randomGenerator) {
        double value = start + nextFraction(randomGenerator) * (end - start);
        return value < end ? value : Math.nextDown(end);
    }

    @Override
    public BigInteger nextOffset(BigInteger bound, RandomGenerator randomGenerator) {
        BigInteger offset = new BigDecimal(bound)
                .multiply(BigDecimal.valueOf(nextFraction(randomGenerator)))
                .toBigInteger();
        return offset.min(bound.subtract(BigInteger.ONE));
    }
}
