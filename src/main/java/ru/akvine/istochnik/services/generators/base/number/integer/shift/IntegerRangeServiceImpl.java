package ru.akvine.istochnik.services.generators.base.number.integer.shift;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IntegerRangeServiceImpl extends AbstractIntegerRangeService<Long, Long> {
    @Override
    public List<Long> range(Long start, Long end, Long step) {
        List<Long> range = new ArrayList<>();
        for (long value = start; value < end; value += step) {
            range.add(value);
        }
        return range;
    }
}
