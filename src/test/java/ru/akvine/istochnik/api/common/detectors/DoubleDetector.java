package ru.akvine.istochnik.api.common.detectors;

import org.springframework.stereotype.Component;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

@Component
public class DoubleDetector extends AbstractDetector<Double> {
    @Override
    public boolean isRandom(List<Double> values) {
        return !isShifted(values);
    }

    @Override
    public boolean isShifted(List<Double> values) {
        List<Double> cleared = clearOfNull(values);
        if (values.size() == 1) {
            return true;
        }

        boolean shifted = true;
        double diff = cleared.get(1) - cleared.get(0);

        for (int i = 1; i < cleared.size(); i++) {
            if (cleared.get(i) - cleared.get(i - 1) != diff) {
                shifted = false;
                break;
            }
        }

        return shifted;
    }

    @Override
    public BaseType getByType() {
        return BaseType.DOUBLE;
    }
}
