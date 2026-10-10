package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class OkvedRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // Классы ОКВЭД 2 (ОК 029-2014)
    private static final List<String> CLASSES = List.of(
            "01", "02", "03", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "35", "36", "37", "38",
            "39", "41", "42", "43", "45", "46", "47", "49", "50", "51", "52", "53", "55", "56", "58", "59", "60", "61",
            "62", "63", "64", "65", "66", "68", "69", "70", "71", "72", "73", "74", "75", "77", "78", "79", "80", "81",
            "82", "84", "85", "86", "87", "88", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99");

    // Сколько цифр идёт после группы: код вида XX.XX, XX.XX.X или XX.XX.XX
    private static final int MAX_DETAIL_DIGITS = 2;

    // Формат кода: класс, подкласс и группа, затем необязательные подгруппа и вид.
    // Контрольного разряда у ОКВЭД нет, наличие кода в классификаторе не гарантируется
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder okved = new StringBuilder();
        okved.append(CLASSES.get(randomGenerator.nextInt(CLASSES.size())));
        okved.append('.');
        okved.append(randomGenerator.nextInt(1, 10));
        okved.append(randomGenerator.nextInt(10));

        int detailDigits = randomGenerator.nextInt(MAX_DETAIL_DIGITS + 1);
        if (detailDigits > 0) {
            okved.append('.');
            for (int i = 0; i < detailDigits; i++) {
                okved.append(randomGenerator.nextInt(1, 10));
            }
        }

        return okved.toString();
    }
}
