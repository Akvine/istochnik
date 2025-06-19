package ru.akvine.istochnik.services.impl.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.converters.StringConvertersProvider;
import ru.akvine.istochnik.services.ConverterService;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
@RequiredArgsConstructor
public class StringConverterService implements ConverterService {
    private final StringConvertersProvider stringConvertersProvider;

    @Override
    public List<?> apply(List<?> generatedValues, List<Converter> converters, RandomGenerator randomGenerator) {
        List<?> values = generatedValues;
        for (Converter converter : converters) {
            values = stringConvertersProvider.getConverter(converter.getName()).convert(
                    (List<String>) values,
                    mapArguments(converter.getArguments()),
                    randomGenerator
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
