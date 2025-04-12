package ru.akvine.istochnik.services.impl.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.managers.filters.DoubleFiltersManager;
import ru.akvine.istochnik.services.FilterService;
import ru.akvine.istochnik.services.dto.Filter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoubleFilterService implements FilterService {
    private final DoubleFiltersManager doubleFiltersManager;

    @Override
    public List<?> apply(List<?> generatedValues, List<Filter> filters) {
        List<?> values = generatedValues;
        for (Filter filter : filters) {
            values = doubleFiltersManager.getFilter(filter.getName()).filter(
                    (List<Double>) values,
                    mapArguments(filter.getArguments())
            );
        }

        return generatedValues;
    }

    @Override
    public BaseType getType() {
        return BaseType.DOUBLE;
    }

    private Double[] mapArguments(Object[] arguments) {
        Double[] array = new Double[arguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            array[i] = (Double) arguments[i];
        }

        return array;
    }
}
