package ru.akvine.istochnik.services.filters.string.hash;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;
import ru.akvine.istochnik.services.filters.string.StringFilter;
import ru.akvine.istochnik.utils.CryptoUtils;

import java.util.List;

@Service
public class SHA256Filter extends StringFilter<String, String> {
    @Override
    public List<String> filter(List<String> input, String[] arguments) {
        return input.stream().map(CryptoUtils::hashSHA256).toList();
    }

    @Override
    public FilterType getName() {
        return FilterType.SHA256;
    }
}
