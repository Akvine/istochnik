package ru.akvine.istochnik.controllers;

import org.springframework.web.bind.annotation.RestController;
import ru.akvine.istochnik.controllers.meta.GeneratorControllerMeta;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.services.generators.date.localdatetime.LocalDateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.number.integer.IntegerGeneratorService;
import ru.akvine.istochnik.services.generators.number.integer.IntegerShiftRange;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;

import java.util.List;
import java.util.Map;

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
        Map<String, Double> filters = Map.of("plus", 19D, "minus", 1D, "pow", 0.5);
        config.setFiltersWithValues(filters);
//        IntegerConstantsConfig config = new IntegerConstantsConfig();
//        config.setSize(30);
//        config.setValue(15);
//        new PowFilter().filter(List.of(1, 2, 3.5));
        return integerGeneratorService.generate(config);
    }
}
