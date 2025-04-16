package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MinMaxScalingDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        double min = input.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double max = input.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double range = max - min;

        if (range == 0) {
            return input.stream().map(v -> 0.0).collect(Collectors.toList()); // все одинаковые
        }

        return input.stream()
                .map(v -> (v - min) / range)
                .collect(Collectors.toList());
    }

    @Override
    public FilterType getName() {
        return FilterType.MINMAXSCALING;
    }
}
