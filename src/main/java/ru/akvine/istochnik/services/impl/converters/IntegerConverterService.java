package ru.akvine.istochnik.services.impl.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.converters.IntegerConvertersProvider;
import ru.akvine.istochnik.services.ConverterService;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class IntegerConverterService implements ConverterService {
    private final IntegerConvertersProvider integerConvertersProvider;

    @Override
    public List<?> apply(List<?> generatedValues, List<Converter> converters, RandomGenerator randomGenerator) {
        List<?> values = generatedValues;
        for (Converter converter : converters) {
            values = integerConvertersProvider.getConverter(converter.getName()).convert(
                    (List<Long>) values,
                    mapArguments(converter.getArguments()),
                    randomGenerator
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
            array[i] = Double.parseDouble((String) arguments[i]);
        }

        return array;
    }
}
