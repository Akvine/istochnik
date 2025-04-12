package ru.akvine.istochnik.services.handlers;

import lombok.RequiredArgsConstructor;
import nl.flotsam.xeger.Xeger;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.RangeType;
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
public class RegexpGenerationHandler implements GenerationHandler {
    private final FilterServicesManager filterServicesManager;

    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        List<Filter> filters = generateColumn.getFilters();
        Set<String> regexps = generateColumn.getConfig().getRegexps();
        int size = generateColumn.getConfig().getSize();
        List<String> generatedValues = new ArrayList<>();
        RangeType rangeType = generateColumn.getConfig().getRangeType();

        while (generatedValues.size() != size) {
            if (rangeType == RangeType.RANDOM) {
                String regexp = CollectionUtils.getRandomElement(regexps);
                Xeger generator = new Xeger(regexp);
                String generated = generator.generate();
                generatedValues.add(generated);
            } else {
                regexps.forEach(regexp -> {
                    Xeger generator = new Xeger(regexp);
                    String generated = generator.generate();
                    generatedValues.add(generated);
                });
            }
        }

        return filterServicesManager.getByType(BaseType.STRING).apply(generatedValues, filters);
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.REGEXP;
    }
}
