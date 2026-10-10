package ru.akvine.istochnik.utils;

import lombok.experimental.UtilityClass;

/**
 * Контрольные разряды российских реквизитов
 */
@UtilityClass
public class RequisiteUtils {
    public final int BIK_LENGTH = 9;
    public final int BANK_ACCOUNT_LENGTH = 20;

    /**
     * Позиция контрольного ключа в номере счёта, считая с нуля
     */
    public final int BANK_ACCOUNT_KEY_INDEX = 8;

    private final int OGRNIP_PAYLOAD_LENGTH = 14;
    private final int CONDITIONAL_NUMBER_LENGTH = 3;
    // Условные номера 000-049 принадлежат подразделениям Банка России, остальные - кредитным организациям
    private final int MIN_CREDIT_ORGANIZATION_NUMBER = 50;
    private final int[] BANK_ACCOUNT_WEIGHTS = {7, 1, 3};

    /**
     * Контрольная цифра ОГРНИП: остаток от деления первых 14 цифр на 13, от которого берётся младший разряд
     */
    public int calculateOgrnipCheckDigit(CharSequence payload) {
        return (int) (Long.parseLong(payload.toString()) % 13 % 10);
    }

    public boolean isValidOgrnip(String ogrnip) {
        if (!isDigits(ogrnip, OGRNIP_PAYLOAD_LENGTH + 1)) {
            return false;
        }

        int checkDigit = calculateOgrnipCheckDigit(ogrnip.substring(0, OGRNIP_PAYLOAD_LENGTH));
        return checkDigit == digitAt(ogrnip, OGRNIP_PAYLOAD_LENGTH);
    }

    /**
     * Контрольная цифра ОКПО для кода без контрольного разряда
     */
    public int calculateOkpoCheckDigit(CharSequence payload) {
        int checkDigit = calculateOkpoWeightedSum(payload, 1) % 11;
        if (checkDigit == 10) {
            // Повторный расчёт с весами, сдвинутыми на два разряда
            checkDigit = calculateOkpoWeightedSum(payload, 3) % 11;
            if (checkDigit == 10) {
                checkDigit = 0;
            }
        }

        return checkDigit;
    }

    public boolean isValidOkpo(String okpo) {
        if (okpo == null || okpo.length() < 2 || !isDigits(okpo, okpo.length())) {
            return false;
        }

        int payloadLength = okpo.length() - 1;
        return calculateOkpoCheckDigit(okpo.substring(0, payloadLength)) == digitAt(okpo, payloadLength);
    }

    /**
     * Контрольный ключ номера счёта для указанного БИК. Цифра, стоящая в счёте на месте ключа, в расчёте не участвует
     */
    public int calculateBankAccountKey(String bik, CharSequence account) {
        String conditionalNumber = resolveConditionalNumber(bik);

        int sum = 0;
        for (int i = 0; i < CONDITIONAL_NUMBER_LENGTH + BANK_ACCOUNT_LENGTH; i++) {
            int digit;
            if (i < CONDITIONAL_NUMBER_LENGTH) {
                digit = digitAt(conditionalNumber, i);
            } else if (i - CONDITIONAL_NUMBER_LENGTH == BANK_ACCOUNT_KEY_INDEX) {
                digit = 0;
            } else {
                digit = digitAt(account, i - CONDITIONAL_NUMBER_LENGTH);
            }

            // Суммируются младшие разряды произведений цифр на веса
            sum += digit * BANK_ACCOUNT_WEIGHTS[i % BANK_ACCOUNT_WEIGHTS.length] % 10;
        }

        return sum % 10 * 3 % 10;
    }

    public boolean isValidBankAccount(String bik, String account) {
        if (!isDigits(bik, BIK_LENGTH) || !isDigits(account, BANK_ACCOUNT_LENGTH)) {
            return false;
        }

        return calculateBankAccountKey(bik, account) == digitAt(account, BANK_ACCOUNT_KEY_INDEX);
    }

    public boolean isDigits(String value, int length) {
        return value != null && value.length() == length && value.chars().allMatch(Character::isDigit);
    }

    // Для счетов в кредитной организации берутся три последние цифры БИК,
    // для счетов в подразделении Банка России - ноль и пятая с шестой цифры БИК
    private String resolveConditionalNumber(String bik) {
        String lastDigits = bik.substring(BIK_LENGTH - CONDITIONAL_NUMBER_LENGTH);
        if (Integer.parseInt(lastDigits) < MIN_CREDIT_ORGANIZATION_NUMBER) {
            return "0" + bik.substring(4, 6);
        }

        return lastDigits;
    }

    private int calculateOkpoWeightedSum(CharSequence payload, int firstWeight) {
        int sum = 0;
        for (int i = 0; i < payload.length(); i++) {
            // Веса идут по кругу от 1 до 10
            int weight = (firstWeight - 1 + i) % 10 + 1;
            sum += digitAt(payload, i) * weight;
        }

        return sum;
    }

    private int digitAt(CharSequence value, int index) {
        return Character.getNumericValue(value.charAt(index));
    }
}
