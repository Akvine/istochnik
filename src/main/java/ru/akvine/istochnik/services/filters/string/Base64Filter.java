package ru.akvine.istochnik.services.filters.string;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class Base64Filter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream()
                .map(value -> new String(Base64.getEncoder().encode(value.getBytes())))
                .toList();
    }

    @Override
    public String getName() {
        return "base64";
    }
}
