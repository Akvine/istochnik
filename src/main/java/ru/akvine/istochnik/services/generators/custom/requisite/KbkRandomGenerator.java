package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;

@Service
public class KbkRandomGenerator extends AbstractRequisiteRandomGenerator<RequisiteGeneratorConfig> {
    // Главные администраторы доходов: ФНС, ФТС, МВД, Казначейство и другие ведомства
    private static final List<String> ADMINISTRATORS =
            List.of("048", "100", "141", "153", "161", "182", "188", "321", "322", "797");
    // Группа доходов: налоговые и неналоговые доходы
    private static final char INCOME_GROUP = '1';
    // Группа подвида доходов: платёж, пени, проценты, штрафы, прочие поступления, возвраты
    private static final List<String> SUBTYPE_GROUPS = List.of("1000", "2100", "2200", "3000", "4000", "5000");
    private static final List<String> ANALYTIC_GROUPS = List.of("110", "120", "130", "140", "150", "160", "170", "180");

    // Формат КБК доходов (20 цифр): главный администратор, группа, подгруппа, статья, подстатья, элемент,
    // группа подвида и аналитическая группа подвида. Контрольного разряда у КБК нет
    @Override
    protected String generateValue(RequisiteGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder kbk = new StringBuilder();
        kbk.append(ADMINISTRATORS.get(randomGenerator.nextInt(ADMINISTRATORS.size())));
        kbk.append(INCOME_GROUP);
        appendNumber(kbk, 1, 17, 2, randomGenerator);
        appendNumber(kbk, 1, 12, 2, randomGenerator);
        // Подстатьи нумеруются десятками
        appendNumber(kbk, 1, 99, 2, randomGenerator);
        kbk.append('0');
        // Элемент - уровень бюджета
        appendNumber(kbk, 1, 14, 2, randomGenerator);
        kbk.append(SUBTYPE_GROUPS.get(randomGenerator.nextInt(SUBTYPE_GROUPS.size())));
        kbk.append(ANALYTIC_GROUPS.get(randomGenerator.nextInt(ANALYTIC_GROUPS.size())));

        return kbk.toString();
    }
}
