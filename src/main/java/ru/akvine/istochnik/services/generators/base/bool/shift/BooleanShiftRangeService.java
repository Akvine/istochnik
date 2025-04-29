package ru.akvine.istochnik.services.generators.base.bool.shift;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BooleanShiftRangeService {
    public List<Boolean> range(boolean start, int size) {
        List<Boolean> generatedValues = new ArrayList<>(size);

        for (int i = 0; i < size; ++i) {
            generatedValues.add(start);
            start = !start;
        }

        return generatedValues;
    }
}
