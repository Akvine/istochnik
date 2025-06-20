package ru.akvine.istochnik.api.detectors;

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
        if (values.size() == 1) {
            return true;
        }

        boolean shifted = true;
        double diff = values.get(1) - values.get(0);

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) - values.get(i - 1) != diff) {
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
