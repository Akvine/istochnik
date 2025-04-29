package ru.akvine.istochnik.services.generators.base.number.integer.shift;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegerRangeServiceImpl extends AbstractIntegerRangeService<Integer, Integer> {
    @Override
    public List<Integer> range(Integer start, Integer end, Integer step) {
        List<Integer> range = new ArrayList<>();
        for (int value = start; value < end; value += step) {
            range.add(value);
        }
        return range;
    }
}
