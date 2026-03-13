package ru.akvine.istochnik.services.converters.string;

import java.util.List;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
@RequiredArgsConstructor
public class RandomCaseConverter extends StringConverter<String, String> {

    @Override
    public List<String> convert(
            List<String> input, String[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (randomGenerator.nextDouble() < probability) {
                        StringBuilder sb = new StringBuilder();
                        char[] chars = value.toCharArray();
                        for (char charValue : chars) {
                            if (Character.isAlphabetic(charValue)) {
                                boolean changeCase = randomGenerator.nextBoolean();

                                if (changeCase && Character.isUpperCase(charValue)) {
                                    sb.append(Character.toLowerCase(charValue));
                                } else if (changeCase && Character.isLowerCase(charValue)) {
                                    sb.append(Character.toUpperCase(charValue));
                                } else {
                                    sb.append(charValue);
                                }
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
        return ConverterType.RANDOM_CASE;
    }
}
