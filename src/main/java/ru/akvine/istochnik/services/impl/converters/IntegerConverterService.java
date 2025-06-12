package ru.akvine.istochnik.services.impl.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.converters.IntegerConvertersProvider;
import ru.akvine.istochnik.services.ConverterService;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegerConverterService implements ConverterService {
    private final IntegerConvertersProvider integerConvertersProvider;

    @Override
    public List<?> apply(List<?> generatedValues, List<Converter> converters) {
        List<?> values = generatedValues;
        for (Converter converter : converters) {
            values = integerConvertersProvider.getConverter(converter.getName()).convert(
                    (List<Integer>) values,
                    mapArguments(converter.getArguments())
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
