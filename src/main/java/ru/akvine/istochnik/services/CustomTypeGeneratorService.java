package ru.akvine.istochnik.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.datetime.DateTimeGeneratorService;
import ru.akvine.istochnik.services.generators.datetime.configs.DateTimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.time.TimeGeneratorService;
import ru.akvine.istochnik.services.generators.time.configs.TimeGeneratorConfig;
import ru.akvine.istochnik.services.generators.uuid.UuidGeneratorService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomTypeGeneratorService {
    private final UuidGeneratorService uuidGeneratorService;
    private final DateTimeGeneratorService dateTimeGeneratorService;
    private final TimeGeneratorService timeGeneratorService;
    private final ConfigMapperService configMapperService;

    public List<?> generate(CustomType type, Config config, List<Filter> filters) {
        return switch (type) {
            case UUID -> uuidGeneratorService.generate(config.getSize(), filters);
            case DATETIME -> {
                DateTimeGeneratorConfig dateTimeGeneratorConfig = configMapperService.createDateTimeConfig(config);
                yield dateTimeGeneratorService.generate(dateTimeGeneratorConfig);
            }
            case TIME -> {
                TimeGeneratorConfig timeGeneratorConfig = configMapperService.createTimeGeneratorConfig(config);
                yield timeGeneratorService.generate(timeGeneratorConfig);
            }
        };
    }
}
