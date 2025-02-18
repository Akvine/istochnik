package ru.akvine.istochnik.services.filters;

import java.util.List;

public abstract class NumberFilter<T extends Number> {
    public abstract List<T> filter(List<T> input, double value);

    public abstract String getName();
}
