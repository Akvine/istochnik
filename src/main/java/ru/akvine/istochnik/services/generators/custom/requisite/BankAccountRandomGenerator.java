package ru.akvine.istochnik.services.generators.custom.requisite;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.utils.RequisiteUtils;

@Service
public class BankAccountRandomGenerator extends AbstractRequisiteRandomGenerator<BankAccountGeneratorConfig> {
    // Балансовые счета расчётных счетов организаций и индивидуальных предпринимателей
    private static final List<String> BALANCE_ACCOUNTS = List.of("40701", "40702", "40703", "40802");
    private static final String RUBLE_CODE = "810";

    // Формат счёта: балансовый счёт, код валюты, контрольный ключ, код подразделения банка, номер лицевого счёта
    @Override
    protected String generateValue(BankAccountGeneratorConfig config) {
        RandomGenerator randomGenerator = config.getRandomGenerator();

        StringBuilder account = new StringBuilder(RequisiteUtils.BANK_ACCOUNT_LENGTH);
        account.append(BALANCE_ACCOUNTS.get(randomGenerator.nextInt(BALANCE_ACCOUNTS.size())));
        account.append(RUBLE_CODE);
        account.append('0');
        appendDigits(account, 4, randomGenerator);
        appendDigits(account, 7, randomGenerator);

        int key;
        if (config.isValid()) {
            if (!RequisiteUtils.isDigits(config.getBik(), RequisiteUtils.BIK_LENGTH)) {
                throw new GenerationException("field [start] must contain BIK of 9 digits");
            }

            key = RequisiteUtils.calculateBankAccountKey(config.getBik(), account);
        } else {
            key = randomGenerator.nextInt(10);
        }
        account.setCharAt(RequisiteUtils.BANK_ACCOUNT_KEY_INDEX, Character.forDigit(key, 10));

        return account.toString();
    }
}
