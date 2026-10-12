package ru.akvine.istochnik.services.distribution;

import java.util.random.RandomGenerator;

/**
 * Нормальное распределение, усечённое границами диапазона: значения собраны вокруг среднего
 * и встречаются тем реже, чем дальше от него
 */
public final class NormalDistribution extends AbstractFractionDistribution {
    private final double mean;
    private final double stddev;

    /**
     * @param mean   положение среднего в диапазоне: 0 - начало, 1 - конец
     * @param stddev стандартное отклонение в долях ширины диапазона
     */
    public NormalDistribution(double mean, double stddev) {
        // Вне этих границ отбор значений в nextFraction мог бы не завершиться
        if (!(mean >= 0 && mean <= 1)) {
            throw new IllegalArgumentException("mean = [" + mean + "] must be between 0 and 1");
        }
        if (!(stddev > 0 && Double.isFinite(stddev))) {
            throw new IllegalArgumentException("stddev = [" + stddev + "] must be greater than 0");
        }

        this.mean = mean;
        this.stddev = stddev;
    }

    // Значения за границами диапазона отбрасываются. Чтобы отбрасывать приходилось редко, при узком колоколе
    // кандидаты берутся из самого нормального распределения, а при широком - равномерно по диапазону
    // и принимаются с вероятностью, пропорциональной плотности. В обоих случаях принимается не меньше трети кандидатов
    @Override
    protected double nextFraction(RandomGenerator randomGenerator) {
        while (true) {
            if (stddev < 1) {
                double candidate = mean + stddev * randomGenerator.nextGaussian();
                if (candidate >= 0 && candidate < 1) {
                    return candidate;
                }
            } else {
                double candidate = randomGenerator.nextDouble();
                double deviation = (candidate - mean) / stddev;
                if (randomGenerator.nextDouble() < Math.exp(-deviation * deviation / 2)) {
                    return candidate;
                }
            }
        }
    }
}
