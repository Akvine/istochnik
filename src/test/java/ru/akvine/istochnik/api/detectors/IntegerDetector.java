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
        List<Long> cleared = clearOfNull(values);
        if (cleared.size() == 1) {
            return true;
        }

        boolean shifted = true;
        long diff = cleared.get(1) - cleared.get(0);

        for (int i = 1; i < cleared.size(); i++) {
            if (cleared.get(i) == null) {
                continue;
            }

            if (cleared.get(i) - cleared.get(i - 1) != diff) {
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
