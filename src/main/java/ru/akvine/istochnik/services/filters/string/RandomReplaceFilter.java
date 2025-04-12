package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.FilterType;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class RandomReplaceFilter extends StringFilter<String, String> {
    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final static char[] SPECIAL_CHARACTERS = {
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
            ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
    };
    private final static List<Character> ENGLISH_CHARS = Alphabets.ENGLISH.getChars();

    private final Random random = new SecureRandom();

    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                int type = random.nextInt(3);

                if (type == 0) {
                    int randomIndex = random.nextInt(ENGLISH_CHARS.size());
                    sb.append(ENGLISH_CHARS.get(randomIndex));
                } else if (type == 1) {
                    int randomIndex = random.nextInt(SPECIAL_CHARACTERS.length);
                    sb.append(SPECIAL_CHARACTERS[randomIndex]);
                } else {
                    int randomIndex = random.nextInt(DIGITS.length);
                    sb.append(DIGITS[randomIndex]);
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.RANDOM_REPLACE;
    }
}
