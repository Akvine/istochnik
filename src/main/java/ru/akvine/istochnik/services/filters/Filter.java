package ru.akvine.istochnik.services.filters;

import ru.akvine.istochnik.enums.FilterType;

import java.util.List;

public abstract class Filter<T, A> {
    public abstract List<T> filter(List<T> input, A[] arguments);

    public abstract FilterType getName();
}
