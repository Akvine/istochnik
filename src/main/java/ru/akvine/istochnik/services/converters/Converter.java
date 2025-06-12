package ru.akvine.istochnik.services.converters;

import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

public abstract class Converter<T, A> {
    public abstract List<T> convert(List<T> input, A[] arguments);

    public abstract ConverterType getName();
}
