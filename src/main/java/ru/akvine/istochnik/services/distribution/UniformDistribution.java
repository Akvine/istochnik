package ru.akvine.istochnik.services.distribution;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

/**
 * Равномерное распределение: все значения диапазона равновероятны. Используется, если в запросе распределение
 * не задано, поэтому случайные числа запрашиваются у генератора теми же вызовами, что и до появления распределений:
 * значения, полученные с одним и тем же seed, не меняются
 */
public final class UniformDistribution implements Distribution {
    public static final UniformDistribution INSTANCE = new UniformDistribution();

    private UniformDistribution() {}

    @Override
    public int nextInt(int start, int end, RandomGenerator randomGenerator) {
        return randomGenerator.nextInt(start, end);
    }

    @Override
    public long nextLong(long start, long end, RandomGenerator randomGenerator) {
        return randomGenerator.nextLong(start, end);
    }

    @Override
    public double nextDouble(double start, double end, RandomGenerator randomGenerator) {
        return randomGenerator.nextDouble(start, end);
    }

    @Override
    public BigInteger nextOffset(BigInteger bound, RandomGenerator randomGenerator) {
        int bitLength = bound.bitLength();
        byte[] bytes = new byte[(bitLength + 7) / 8];
        int excessBits = bytes.length * 8 - bitLength;

        BigInteger value;
        do {
            randomGenerator.nextBytes(bytes);
            bytes[0] &= (byte) (0xFF >>> excessBits);
            value = new BigInteger(1, bytes);
        } while (value.compareTo(bound) >= 0);

        return value;
    }
}
