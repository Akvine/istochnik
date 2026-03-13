package ru.akvine.istochnik.services.converters.doubles;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

@Service
public class MinMaxScalingDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(
            List<Double> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        double min = input.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double max = input.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
        double range = max - min;

        if (range == 0) {
            return input.stream().map(v -> 0.0).collect(Collectors.toList()); // все одинаковые
        }

        return input.stream()
                .map(v -> randomGenerator.nextDouble() < probability ? (v - min) / range : v)
                .collect(Collectors.toList());
    }

    @Override
    public ConverterType getName() {
        return ConverterType.MINMAXSCALING;
    }
}
