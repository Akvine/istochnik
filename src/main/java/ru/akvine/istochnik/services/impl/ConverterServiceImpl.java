package ru.akvine.istochnik.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.ConverterService;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.impl.filters.StringFilterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConverterServiceImpl implements ConverterService {
    private final StringFilterService stringFilterService;

    @Override
    public List<String> convert(List<?> inputValues, List<Filter> filtersToApply) {
        List<String> converted = inputValues.stream().map(String::valueOf).toList();
        return (List<String>) stringFilterService.apply(converted, filtersToApply);
    }
}
