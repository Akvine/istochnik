package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Общая часть генераторов кодов территорий (ОКТМО и ОКАТО): первые две цифры у обоих - код субъекта РФ
 */
public abstract class AbstractTerritoryCodeRandomGenerator
        extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    private static final List<String> REGION_CODES = List.of(
            "01", "03", "04", "05", "07", "08", "10", "11", "12", "14", "15", "17", "18", "19", "20", "22", "24", "25",
            "26", "27", "28", "29", "30", "32", "33", "34", "35", "36", "37", "38", "40", "41", "42", "44", "45", "46",
            "47", "49", "50", "52", "53", "54", "56", "57", "58", "60", "61", "63", "64", "65", "66", "67", "68", "69",
            "70", "71", "73", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99");

    protected void appendRegion(StringBuilder builder, RandomGenerator randomGenerator) {
        builder.append(REGION_CODES.get(randomGenerator.nextInt(REGION_CODES.size())));
    }
}
