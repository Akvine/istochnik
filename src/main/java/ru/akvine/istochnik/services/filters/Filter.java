package ru.akvine.istochnik.services.filters;

import java.util.List;

public abstract class Filter<T, A> {
    public abstract List<T> filter(List<T> input, A[] arguments);

    public abstract String getName();
}
