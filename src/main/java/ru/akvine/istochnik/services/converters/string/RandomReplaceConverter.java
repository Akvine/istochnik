package ru.akvine.istochnik.services.converters.string;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class RandomReplaceConverter extends StringConverter<String, String> {
    private final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final static char[] SPECIAL_CHARACTERS = {
            '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/',
            ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
    };
    private final static List<Character> ENGLISH_CHARS = Alphabets.ENGLISH.getChars();


    @Override
    public List<String> convert(List<String> input, String[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                int type = randomGenerator.nextInt(3);

                if (type == 0) {
                    int randomIndex = randomGenerator.nextInt(ENGLISH_CHARS.size());
                    sb.append(ENGLISH_CHARS.get(randomIndex));
                } else if (type == 1) {
                    int randomIndex = randomGenerator.nextInt(SPECIAL_CHARACTERS.length);
                    sb.append(SPECIAL_CHARACTERS[randomIndex]);
                } else {
                    int randomIndex = randomGenerator.nextInt(DIGITS.length);
                    sb.append(DIGITS[randomIndex]);
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.RANDOM_REPLACE;
    }
}
