package ru.akvine.istochnik.services.distribution;

import java.util.random.RandomGenerator;

/**
 * Экспоненциальное распределение, усечённое границами диапазона: значения чаще всего встречаются у одной
 * из границ, а по мере удаления от неё - всё реже
 */
public final class ExponentialDistribution extends AbstractFractionDistribution {
    private final double scale;
    private final boolean peakAtEnd;

    /**
     * @param scale     среднее расстояние значения от пика в долях ширины диапазона. У экспоненциального
     *                  распределения оно равно стандартному отклонению
     * @param peakAtEnd значения сгущаются у конца диапазона, а не у начала
     */
    public ExponentialDistribution(double scale, boolean peakAtEnd) {
        if (!(scale > 0 && Double.isFinite(scale))) {
            throw new IllegalArgumentException("scale = [" + scale + "] must be greater than 0");
        }

        this.scale = scale;
        this.peakAtEnd = peakAtEnd;
    }

    // Обратная функция распределения, усечённого по диапазону: на значение уходит одно случайное число,
    // и результат всегда попадает в диапазон
    @Override
    protected double nextFraction(RandomGenerator randomGenerator) {
        double distance = -scale * Math.log1p(randomGenerator.nextDouble() * Math.expm1(-1 / scale));
        double fraction = peakAtEnd ? 1 - distance : distance;
        return Math.min(Math.max(fraction, 0), MAX_FRACTION);
    }
}
