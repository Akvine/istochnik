package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.compozit.commons.utils.MathUtils;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

@Service
public class RoundDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        if (arguments != null && arguments.length != 0 && arguments[0] != null) {
            int accuracy = arguments[0].intValue();
            return input.stream().map(value -> MathUtils.round(value, accuracy)).toList();
        } else {
            return input.stream().map(MathUtils::round).toList();
        }
    }

    @Override
    public FilterType getName() {
        return FilterType.ROUND;
    }
}
