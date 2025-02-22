package ru.akvine.istochnik.validators;

public interface Validator<T> {
    void validate(T object);
}
