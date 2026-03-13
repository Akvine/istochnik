package ru.akvine.istochnik.services.converters;

import java.util.List;
import java.util.random.RandomGenerator;
import ru.akvine.istochnik.enums.ConverterType;

public abstract class Converter<T, A> {
    public abstract List<T> convert(List<T> input, A[] arguments, RandomGenerator randomGenerator, double probability);

    public abstract ConverterType getName();

    public void validateArgument(A[] arguments) {}
}
