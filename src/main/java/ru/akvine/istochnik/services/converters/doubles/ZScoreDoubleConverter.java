package ru.akvine.istochnik.services.converters.doubles;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

@Service
public class ZScoreDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(List<Double> input, Double[] arguments, RandomGenerator randomGenerator) {
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
    public ConverterType getName() {
        return ConverterType.ZSCORE;
    }
}
