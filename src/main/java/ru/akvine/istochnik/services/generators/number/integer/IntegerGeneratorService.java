package ru.akvine.istochnik.services.generators.number.integer;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.IntegerFiltersManager;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerConstantsConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.random.IntegerRandomGenerator;
import ru.akvine.istochnik.services.generators.number.integer.shift.AbstractIntegerRangeService;

import java.util.List;
import java.util.Map;

@Service
public class IntegerGeneratorService {
    private final IntegerRandomGenerator integerRandomGenerator;
    private final AbstractIntegerRangeService<Integer, Integer> integerRangeService;
    private final IntegerFiltersManager integerFiltersManager;

    public IntegerGeneratorService(IntegerRandomGenerator integerRandomGenerator,
                                   AbstractIntegerRangeService<Integer, Integer> integerRangeService,
                                   IntegerFiltersManager integerFiltersManager) {
        this.integerRandomGenerator = integerRandomGenerator;
        this.integerRangeService = integerRangeService;
        this.integerFiltersManager = integerFiltersManager;
    }

    public List<Integer> generate(IntegerConstantsConfig config) {
        return new ConstantGenerator<Integer>().generate(config.getSize(), config.getValue());
    }

    public List<Integer> generate(IntegerGeneratorConfig config) {
        if (config.getRangeType() == RangeType.RANDOM) {
            return (List<Integer>) integerRandomGenerator.generate(config);
        } else {
            Map<String, Double> filtersWithValues = config.getFiltersWithValues();
            List<Integer> generatedValues = (List<Integer>) integerRangeService.range(
                    config.getIntegerShiftRange().getStart(),
                    config.getIntegerShiftRange().getEnd(),
                    config.getIntegerShiftRange().getStep());

            if (!CollectionUtils.isEmpty(filtersWithValues)) {
                for (Map.Entry<String, Double> entry : filtersWithValues.entrySet()) {
                    generatedValues = integerFiltersManager.getByType(entry.getKey())
                            .filter(generatedValues, entry.getValue());
                }
            }

            return generatedValues;
        }
    }
}
