package ru.akvine.istochnik.services.converters.integer;

import java.util.List;
import java.util.random.RandomGenerator;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

// TODO: добавить второй необязательный параметр. Если он добавлен - должно генерироваться число для суммирования между ними
@Service
public class PlusIntegerConverter extends IntegerConverter<Long, Double> {
    @Override
    public List<Long> convert(
            List<Long> input, Double[] argument, RandomGenerator randomGenerator, double probability) {
        return input.stream()
                .map(value -> {
                    if (value == null) {
                        return null;
                    } else {
                        if (randomGenerator.nextDouble() < probability) {
                            return (long) (value + argument[0]);
                        } else {
                            return value;
                        }
                    }
                })
                .toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.PLUS;
    }

    @Override
    public void validateArgument(Double[] arguments) {
        if (arguments == null || arguments.length == 0 || arguments[0] == null) {
            throw new IllegalArgumentException("arguments can't be null or empty!");
        }
    }
}
