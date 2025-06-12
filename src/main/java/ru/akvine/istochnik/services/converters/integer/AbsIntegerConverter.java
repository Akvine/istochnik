package ru.akvine.istochnik.services.converters.integer;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.Converter;

import java.util.List;

@Service
public class AbsIntegerConverter extends Converter<Long, Void> {
    @Override
    public List<Long> convert(List<Long> input, Void[] argument) {
        return input.stream().map(Math::abs).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ABS;
    }
}
