package ru.akvine.istochnik.services.impl.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.converters.DoubleConvertersProvider;
import ru.akvine.istochnik.services.ConverterService;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoubleConverterService implements ConverterService {
    private final DoubleConvertersProvider doubleConvertersProvider;

    @Override
    public List<?> apply(List<?> generatedValues, List<Converter> converters) {
        List<?> values = generatedValues;
        for (Converter converter : converters) {
            values = doubleConvertersProvider.getConverter(converter.getName()).convert(
                    (List<Double>) values,
                    mapArguments(converter.getArguments())
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
