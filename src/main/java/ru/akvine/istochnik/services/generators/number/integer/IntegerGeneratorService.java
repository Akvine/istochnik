package ru.akvine.istochnik.services.generators.number.integer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.filters.IntegerFiltersManager;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.ConstantGenerator;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerConstantsConfig;
import ru.akvine.istochnik.services.generators.number.integer.configs.IntegerGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.integer.random.IntegerRandomGenerator;
import ru.akvine.istochnik.services.generators.number.integer.shift.AbstractIntegerRangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegerGeneratorService {
    private final IntegerRandomGenerator integerRandomGenerator;
    private final AbstractIntegerRangeService<Integer, Integer> integerRangeService;
    private final IntegerFiltersManager integerFiltersManager;

    public List<Integer> generate(IntegerConstantsConfig config) {
        return new ConstantGenerator<Integer>().generate(config.getSize(), config.getValue());
    }

    public List<Integer> generate(IntegerGeneratorConfig config, List<Filter> filters) {
        List<Integer> generatedValues;

        if (config.getRangeType() == RangeType.RANDOM) {
            generatedValues = (List<Integer>) integerRandomGenerator.generate(config);
        } else {
            generatedValues = (List<Integer>) integerRangeService.range(
                    config.getIntegerShiftRange().getStart(),
                    config.getIntegerShiftRange().getEnd(),
                    config.getIntegerShiftRange().getStep());
        }

        if (!CollectionUtils.isEmpty(filters)) {
            for (Filter filter : filters) {
                // TODO: Code-smells. Придумать что-нибудь по лучше
                if (StringUtils.isNotBlank(filter.getArgument1())) {
                    generatedValues = integerFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new Double[]{Double.parseDouble(filter.getArgument1())});
                } else if (StringUtils.isNotBlank(filter.getArgument1()) && StringUtils.isNotBlank(filter.getArgument2())) {
                    generatedValues = integerFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new Double[]
                                    {
                                            Double.parseDouble(filter.getArgument1()),
                                            Double.parseDouble(filter.getArgument2())
                                    }
                            );
                } else {
                    generatedValues = integerFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new Double[]{});
                }
            }
        }

        return generatedValues;
    }
}
