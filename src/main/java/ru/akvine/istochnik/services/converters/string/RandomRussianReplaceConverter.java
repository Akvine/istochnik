package ru.akvine.istochnik.services.converters.string;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class RandomRussianReplaceConverter extends StringConverter<String, String> {
    private final static List<Character> RUSSIAN_CHARS = Alphabets.RUSSIAN.getChars();

    @Override
    public List<String> convert(List<String> input, String[] arguments, RandomGenerator randomGenerator) {
        return input.stream().map(value -> {
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
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.RANDOM_RUSSIAN_REPLACE;
    }
}
