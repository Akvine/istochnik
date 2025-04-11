package ru.akvine.istochnik.services.generators.number.doubles;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.managers.filters.DoubleFiltersManager;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.number.doubles.configs.DoubleGeneratorConfig;
import ru.akvine.istochnik.services.generators.number.doubles.random.DoubleRandomGenerator;
import ru.akvine.istochnik.services.generators.number.doubles.shift.DoubleRangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoubleGeneratorService {
    private final DoubleRandomGenerator doubleRandomGenerator;
    private final DoubleRangeService doubleRangeService;
    private final DoubleFiltersManager doubleFiltersManager;

    public List<Double> generate(DoubleGeneratorConfig config, List<Filter> filters) {
        List<Double> generatedValues;
        if (config.getRangeType() == RangeType.RANDOM) {
            generatedValues = doubleRandomGenerator.generate(config);
        } else {
            generatedValues = doubleRangeService.range(
                    config.getDoubleShiftRange().getStart(),
                    config.getDoubleShiftRange().getEnd(),
                    config.getDoubleShiftRange().getStep());
        }

        if (!CollectionUtils.isEmpty(filters)) {
            for (Filter filter : filters) {
                // TODO: Code-smells. Придумать что-нибудь по лучше
                if (StringUtils.isNotBlank(filter.getArgument1())) {
                    generatedValues = doubleFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new Double[]{Double.parseDouble(filter.getArgument1())});
                } else if (StringUtils.isNotBlank(filter.getArgument1()) && StringUtils.isNotBlank(filter.getArgument2())) {
                    generatedValues = doubleFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new Double[]
                                    {
                                            Double.parseDouble(filter.getArgument1()),
                                            Double.parseDouble(filter.getArgument2())
                                    }
                            );
                } else {
                    generatedValues = doubleFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new Double[]{});
                }
            }
        }

        return generatedValues;
    }
}
