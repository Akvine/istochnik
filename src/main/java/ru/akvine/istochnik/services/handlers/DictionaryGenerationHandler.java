package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DictionaryGenerationHandler implements GenerationHandler {
    private final FilterServicesManager filterServicesManager;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        List<Filter> filters = generateColumn.getFilters();
        Set<String> dictionary = generateColumn.getConfig().getDictionary();
        int size = generateColumn.getConfig().getSize();

        List<String> generatedValues = new ArrayList<>();
        while (generatedValues.size() != size) {
            String element = CollectionUtils.getRandomElement(dictionary);
            generatedValues.add(element);
        }

        return filterServicesManager.getByType(BaseType.STRING).apply(generatedValues, filters);
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.DICTIONARY;
    }
}
