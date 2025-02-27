package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpperCaseFilter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(String::toUpperCase).toList();
    }

    @Override
    public String getName() {
        return "upperCase";
    }
}
