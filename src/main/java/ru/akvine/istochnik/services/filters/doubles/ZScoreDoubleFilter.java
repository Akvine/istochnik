package ru.akvine.istochnik.services.filters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FilterType;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZScoreDoubleFilter extends DoubleFilter<Double, Double> {
    @Override
    public List<Double> filter(List<Double> input, Double[] arguments) {
        double mean = input.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double stdDev = Math.sqrt(input.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0));

        if (stdDev == 0) {
            return input.stream().map(v -> 0.0).collect(Collectors.toList());
        }

        return input.stream()
                .map(v -> (v - mean) / stdDev)
                .collect(Collectors.toList());
    }

    @Override
    public FilterType getName() {
        return FilterType.ZSCORE;
    }
}
