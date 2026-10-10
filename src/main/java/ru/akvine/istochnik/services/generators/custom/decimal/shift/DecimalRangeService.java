package ru.akvine.istochnik.services.generators.custom.decimal.shift;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.services.generators.base.number.NumberRangeService;

@Service
public class DecimalRangeService implements NumberRangeService<BigDecimal, BigDecimal> {
    @Override
    public List<BigDecimal> range(BigDecimal start, BigDecimal end, BigDecimal step) {
        List<BigDecimal> range = new ArrayList<>();
        for (BigDecimal value = start; value.compareTo(end) < 0; value = value.add(step)) {
            range.add(value);
        }
        return range;
    }
}
