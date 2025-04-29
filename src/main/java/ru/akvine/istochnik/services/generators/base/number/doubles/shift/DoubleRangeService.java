package ru.akvine.istochnik.services.generators.base.number.doubles.shift;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoubleRangeService extends AbstractDoubleRangeService<Double, Double> {
    @Override
    public List<Double> range(Double start, Double end, Double step) {
        List<Double> range = new ArrayList<>();
        for (double value = start; value < end; value += step) {
            range.add(value);
        }
        return range;
    }
}
