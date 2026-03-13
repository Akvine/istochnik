package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.ConverterType;

@Service
@RequiredArgsConstructor
public class RandomEnglishReplaceConverter extends StringConverter<String, String> {
    private static final List<Character> ENGLISH_CHARS = Alphabets.ENGLISH.getChars();

    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (randomGenerator.nextDouble() < probability) {
                        StringBuilder sb = new StringBuilder();
                        char[] chars = value.toCharArray();
                        for (char charValue : chars) {
                            if (Character.isAlphabetic(charValue) && ENGLISH_CHARS.contains(charValue)) {
                                int randomIndex = randomGenerator.nextInt(ENGLISH_CHARS.size());
                                sb.append(ENGLISH_CHARS.get(randomIndex));
                            } else {
                                sb.append(charValue);
                            }
                        }

                        return sb.toString();
                    } else {
                        return value;
                    }
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.RANDOM_ENGLISH_REPLACE;
    }
}
