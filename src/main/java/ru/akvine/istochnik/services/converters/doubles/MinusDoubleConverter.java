package ru.akvine.istochnik.services.converters.doubles;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

// TODO: добавить второй необязательный параметр. Если он добавлен - должно генерироваться число для разницы между ними
@Service
public class MinusDoubleConverter extends DoubleConverter<Double, Double> {
    @Override
    public List<Double> convert(
            List<Double> input, Double[] arguments, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> randomGenerator.nextDouble() < probability ? value - arguments[0] : value)
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.MINUS;
    }

    @Override
    public void validateArgument(Double[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }
    }
}
