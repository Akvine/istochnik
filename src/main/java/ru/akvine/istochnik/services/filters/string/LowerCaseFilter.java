package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LowerCaseFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(String::toLowerCase).toList();
    }

    @Override
    public String getName() {
        return "lowerCase";
    }
}
