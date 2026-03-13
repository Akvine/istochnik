package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.ConverterType;

@Service
@RequiredArgsConstructor
public class RandomRussianReplaceConverter extends StringConverter<String, String> {
    private static final List<Character> RUSSIAN_CHARS = Alphabets.RUSSIAN.getChars();

    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (randomGenerator.nextDouble() < probability) {
                        StringBuilder sb = new StringBuilder();
                        char[] chars = value.toCharArray();
                        for (char charValue : chars) {
                            if (Character.isAlphabetic(charValue) && RUSSIAN_CHARS.contains(charValue)) {
                                int randomIndex = randomGenerator.nextInt(RUSSIAN_CHARS.size());
                                sb.append(RUSSIAN_CHARS.get(randomIndex));
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
        return ConverterType.RANDOM_RUSSIAN_REPLACE;
    }
}
