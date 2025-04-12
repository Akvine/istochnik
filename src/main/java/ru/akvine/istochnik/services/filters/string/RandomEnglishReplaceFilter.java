package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.FilterType;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class RandomEnglishReplaceFilter extends StringFilter<String, String> {
    private final static Random random = new SecureRandom();
    private final static List<Character> ENGLISH_CHARS = Alphabets.ENGLISH.getChars();

    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isAlphabetic(charValue) && ENGLISH_CHARS.contains(charValue)) {
                    int randomIndex = random.nextInt(ENGLISH_CHARS.size());
                    sb.append(ENGLISH_CHARS.get(randomIndex));
                } else {
                    sb.append(charValue);
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.RANDOM_ENGLISH_REPLACE;
    }
}
