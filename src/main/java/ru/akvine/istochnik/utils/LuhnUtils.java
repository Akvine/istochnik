package ru.akvine.istochnik.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LuhnUtils {
    /**
     * Контрольная цифра по алгоритму Луна для последовательности цифр без контрольного разряда
     */
    public int calculateCheckDigit(CharSequence payload) {
        int sum = 0;
        // Удваивается каждая вторая цифра, начиная с крайней правой
        for (int i = 0; i < payload.length(); i++) {
            int digit = Character.getNumericValue(payload.charAt(payload.length() - 1 - i));

            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
        }

        return (10 - (sum % 10)) % 10;
    }

    /**
     * Проверяет, что последняя цифра номера является корректным контрольным разрядом
     */
    public boolean isValid(String number) {
        if (number == null || number.length() < 2 || !number.chars().allMatch(Character::isDigit)) {
            return false;
        }

        int payloadLength = number.length() - 1;
        int checkDigit = Character.getNumericValue(number.charAt(payloadLength));
        return calculateCheckDigit(number.substring(0, payloadLength)) == checkDigit;
    }
}
