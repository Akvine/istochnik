package ru.akvine.istochnik.api.detectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;

@Component
public class IntegerDetector extends AbstractDetector<Long> {
    @Override
    public boolean isRandom(List<Long> values) {
        return !isShifted(values);
    }

    @Override
    public boolean isShifted(List<Long> values) {
        if (values.size() == 1) {
            return true;
        }

        boolean shifted = true;
        long diff = values.get(1) - values.get(0);

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }

            if (values.get(i) - values.get(i - 1) != diff) {
                shifted = false;
                break;
            }
        }

        return shifted;
    }

    @Override
    public BaseType getByType() {
        return BaseType.INTEGER;
    }
}
