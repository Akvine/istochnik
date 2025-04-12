package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class InversionFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isAlphabetic(charValue) && Character.isLowerCase(charValue)) {
                    sb.append(Character.toUpperCase(charValue));
                } else if (Character.isAlphabetic(charValue) && Character.isUpperCase(charValue)) {
                    sb.append(Character.toLowerCase(charValue));
                } else {
                    sb.append(charValue);
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.INVERSION;
    }
}
