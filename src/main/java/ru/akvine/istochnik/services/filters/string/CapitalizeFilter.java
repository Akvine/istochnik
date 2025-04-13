package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class CapitalizeFilter extends StringFilter<String, String> {

    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            String firstCharacter = value.substring(0, 1).toUpperCase();
            String otherCharacters = value.substring(1);
            return firstCharacter + otherCharacters;
        }).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.CAPITALIZE;
    }
}
