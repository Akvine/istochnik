package ru.akvine.istochnik.services.converters;

import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;
import java.util.random.RandomGenerator;

public abstract class Converter<T, A> {
    public abstract List<T> convert(List<T> input, A[] arguments, RandomGenerator randomGenerator);

    public abstract ConverterType getName();

    public void validateArgument(A[] arguments) {

    }
}
