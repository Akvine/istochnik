package ru.akvine.istochnik.services.distribution;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

/**
 * Закон, по которому случайные значения распределены внутри диапазона [start, end)
 */
public interface Distribution {
    long nextLong(long start, long end, RandomGenerator randomGenerator);

    double nextDouble(double start, double end, RandomGenerator randomGenerator);

    /**
     * Смещение значения от начала диапазона, в котором bound значений: целое из [0, bound).
     * Нужно типам, диапазон которых не помещается в long
     */
    BigInteger nextOffset(BigInteger bound, RandomGenerator randomGenerator);

    default int nextInt(int start, int end, RandomGenerator randomGenerator) {
        return (int) nextLong(start, end, randomGenerator);
    }
}
