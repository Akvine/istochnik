package ru.akvine.istochnik.services.impl.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.managers.filters.StringFiltersManager;
import ru.akvine.istochnik.services.FilterService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StringFilterService implements FilterService {
    private final StringFiltersManager stringFiltersManager;

    @Override
    public List<?> apply(List<?> generatedValues, List<Filter> filters) {
        List<?> values = generatedValues;
        for (Filter filter : filters) {
            values = stringFiltersManager.getFilter(filter.getName()).filter(
                    (List<String>) values,
                    mapArguments(filter.getArguments())
            );
        }

        return values;
    }

    @Override
    public BaseType getType() {
        return BaseType.STRING;
    }

    private String[] mapArguments(Object[] arguments) {
        String[] array = new String[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            array[i] = (String) arguments[i];
        }

        return array;
    }
}
