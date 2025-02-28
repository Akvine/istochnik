package ru.akvine.istochnik.services.generators.uuid;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.akvine.istochnik.managers.filters.StringFiltersManager;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UuidGeneratorService {
    private final StringFiltersManager stringFiltersManager;

    public List<String> generate(int size) {
        return generate(size, List.of());
    }

    public List<String> generate(int size, List<Filter> filters) {
        return generate(new Config(size), filters);
    }

    public List<String> generate(Config config, List<Filter> filters) {
        List<String> generatedValues = new ArrayList<>();

        for (int i = 0; i < config.getSize(); ++i) {
            generatedValues.add(UUID.randomUUID().toString());
        }

        if (!CollectionUtils.isEmpty(filters)) {
            for (Filter filter : filters) {
                // TODO: Code-smells. Придумать что-нибудь по лучше
                if (filter.getArgument1() != null && filter.getArgument2() != null) {
                    generatedValues = stringFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new String[]
                                    {
                                            filter.getArgument1(),
                                            filter.getArgument2()
                                    }
                            );
                } else if (filter.getArgument1() != null) {
                    generatedValues = stringFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new String[]{filter.getArgument1()});
                } else {
                    generatedValues = stringFiltersManager
                            .getFilter(filter.getName()).filter(generatedValues, new String[]{});
                }
            }
        }

        return generatedValues;
    }

    public List<String> generate(Config config, String value) {
        List<String> generatedValues = new ArrayList<>();

        for (int i = 0; i < config.getSize(); ++i) {
            generatedValues.add(value);
        }

        return generatedValues;
    }
}
