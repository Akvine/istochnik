package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.random.RandomGenerator;

/**
 * Общая часть генераторов документов на транспортное средство (СТС и ПТС):
 * код региона, серия из двух знаков и шестизначный номер. Наследник описывает только серию
 */
public abstract class AbstractVehicleDocumentRandomGenerator
        extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // В серии используются только буквы кириллицы, у которых есть латинские аналоги
    private static final String SERIES_LETTERS = "АВЕКМНОРСТУХ";
    private static final int SERIES_LENGTH = 2;
    private static final int NUMBER_LENGTH = 6;

    // Контрольного разряда у СТС и ПТС нет
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder document = new StringBuilder();
        appendNumber(document, 1, 99, 2, randomGenerator);
        appendSeries(document, randomGenerator);
        appendDigits(document, NUMBER_LENGTH, randomGenerator);

        return document.toString();
    }

    protected abstract void appendSeries(StringBuilder builder, RandomGenerator randomGenerator);

    protected void appendSeriesLetters(StringBuilder builder, RandomGenerator randomGenerator) {
        for (int i = 0; i < SERIES_LENGTH; i++) {
            builder.append(SERIES_LETTERS.charAt(randomGenerator.nextInt(SERIES_LETTERS.length())));
        }
    }

    protected void appendSeriesDigits(StringBuilder builder, RandomGenerator randomGenerator) {
        appendDigits(builder, SERIES_LENGTH, randomGenerator);
    }
}
