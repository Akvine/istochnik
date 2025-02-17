package ru.akvine.istochnik.controllers;

import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.meta.GeneratorControllerMeta;
import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeShiftRange;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class GeneratorController implements GeneratorControllerMeta {
    private final LocalDateTimeGeneratorService localDateTimeGeneratorService;

    public GeneratorController(LocalDateTimeGeneratorService localDateTimeGeneratorService) {
        this.localDateTimeGeneratorService = localDateTimeGeneratorService;
    }

    @Override
    public List<?> make() {
        LocalDateTimeShiftRange shiftRange = new LocalDateTimeShiftRange();
        shiftRange.setShiftCount(10);
        shiftRange.setStart(LocalDateTime.now());
        shiftRange.setEnd(LocalDateTime.now().plusYears(50));
        shiftRange.setDateShiftType(DateShiftType.YEAR);
        LocalDateTimeGeneratorConfig config = new LocalDateTimeGeneratorConfig(
                10,
                true,
                true,
                RangeType.SHIFT,
                shiftRange);

        return localDateTimeGeneratorService.generate(config);
    }
}
