package ru.akvine.istochnik.services.generators;

import java.util.ArrayList;
import java.util.List;

public class ConstantGenerator<T> {
    public List<T> generate(int size, T value) {
        List<T> generatedValues = new ArrayList<>(size);

        for (int i = 0; i < size; ++i) {
            generatedValues.add(value);
        }

        return generatedValues;
    }
}
