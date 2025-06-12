package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.MathUtils;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class RoundDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments) {
        if (arguments != null && arguments.length != 0 && arguments[0] != null) {
            int accuracy = arguments[0].intValue();
            return input.stream().map(value -> MathUtils.round(value, accuracy)).toList();
        } else {
            return input.stream().map(MathUtils::round).toList();
        }
    }

    @Override
    public ConverterType getName() {
        return ConverterType.ROUND;
    }
}
