package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class RandomCaseFilter extends StringFilter<String, String> {
    private final static Random random = new SecureRandom();

    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isAlphabetic(charValue)) {
                    boolean changeCase = random.nextBoolean();

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
        }).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.RANDOM_CASE;
    }
}
