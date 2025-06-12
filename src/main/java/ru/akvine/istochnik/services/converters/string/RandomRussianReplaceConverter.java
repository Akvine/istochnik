package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.ConverterType;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class RandomRussianReplaceConverter extends StringConverter<String, String> {
    private final static Random random = new SecureRandom();
    private final static List<Character> RUSSIAN_CHARS = Alphabets.RUSSIAN.getChars();

    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isAlphabetic(charValue) && RUSSIAN_CHARS.contains(charValue)) {
                    int randomIndex = random.nextInt(RUSSIAN_CHARS.size());
                    sb.append(RUSSIAN_CHARS.get(randomIndex));
                } else {
                    sb.append(charValue);
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.RANDOM_RUSSIAN_REPLACE;
    }
}
