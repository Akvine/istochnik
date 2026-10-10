package ru.akvine.istochnik.services.generators.custom.zoneddatetime;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.configs.ZonedDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.custom.zoneddatetime.random.ZonedDateTimeRandomGenerator;

@Service
@RequiredArgsConstructor
public class ZonedDateTimeGeneratorService {
    private final ZonedDateTimeRandomGenerator zonedDateTimeRandomGenerator;

    public List<ZonedDateTime> generate(ZonedDateTimeGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return zonedDateTimeRandomGenerator.generate(config);
        }

        ZonedDateTimeShiftRange shiftRange = config.getZonedDateTimeShiftRange();
        if (shiftRange.getShiftCount() <= 0) {
            throw new GenerationException("field [step] must be greater than 0");
        }

        // Шаг откладывается по оси времени, поэтому переходы на летнее время не дают пропусков и повторов
        List<ZonedDateTime> range = new ArrayList<>(config.getSize());
        ZonedDateTime value = shiftRange.getStart();
        for (int i = 0; i < config.getSize(); ++i) {
            if (shiftRange.getEnd() != null && value.isAfter(shiftRange.getEnd())) {
                break;
            }

            range.add(value);
            value = value.plusSeconds(shiftRange.getShiftCount());
        }

        return range;
    }
}
