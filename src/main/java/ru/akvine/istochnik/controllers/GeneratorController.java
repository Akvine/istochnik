package ru.akvine.istochnik.controllers;

import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.meta.GeneratorControllerMeta;
import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeShiftRange;
import ru.akvine.istochnik.services.generators.date.localdatetime.configs.LocalDateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.IntegerGeneratorService;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerConstantsConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.random.IntegerRandomGenerator;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class GeneratorController implements GeneratorControllerMeta {
    private final LocalDateTimeGeneratorService localDateTimeGeneratorService;
    private final IntegerGeneratorService integerGeneratorService;

    public GeneratorController(LocalDateTimeGeneratorService localDateTimeGeneratorService,
                               IntegerGeneratorService integerGeneratorService) {
        this.localDateTimeGeneratorService = localDateTimeGeneratorService;
        this.integerGeneratorService = integerGeneratorService;
    }

    @Override
    public List<?> make() {
        IntegerShiftRange integerShiftRange = new IntegerShiftRange();
        integerShiftRange.setStart(1);
        integerShiftRange.setEnd(5);
        integerShiftRange.setStep(1);
        IntegerGeneratorConfig config = new IntegerGeneratorConfig(
                4,
                true, true,
                RangeType.SHIFT,
                integerShiftRange);
//        IntegerConstantsConfig config = new IntegerConstantsConfig();
//        config.setSize(30);
//        config.setValue(15);
        return integerGeneratorService.generate(config);
    }
}
