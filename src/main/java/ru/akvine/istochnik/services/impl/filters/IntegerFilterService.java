package ru.akvine.istochnik.services.impl.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.filters.IntegerFiltersProvider;
import ru.akvine.istochnik.services.FilterService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegerFilterService implements FilterService {
    private final IntegerFiltersProvider integerFiltersProvider;

    @Override
    public List<?> apply(List<?> generatedValues, List<Filter> filters) {
        List<?> values = generatedValues;
        for (Filter filter : filters) {
            values = integerFiltersProvider.getFilter(filter.getName()).filter(
                    (List<Integer>) values,
                    mapArguments(filter.getArguments())
            );
        }

        return values;
    }

    @Override
    public BaseType getType() {
        return BaseType.INTEGER;
    }

    private Double[] mapArguments(Object[] arguments) {
        Double[] array = new Double[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            array[i] = (Double) arguments[i];
        }

        return array;
    }
}
