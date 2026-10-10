package ru.akvine.istochnik.services.impl.generators.custom;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;
import ru.akvine.istochnik.services.generators.custom.decimal.DecimalGeneratorService;
import ru.akvine.istochnik.services.generators.custom.decimal.configs.DecimalGeneratorConfig;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

@Service
public class DecimalRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final DecimalGeneratorService decimalGeneratorService;

    protected DecimalRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            DecimalGeneratorService decimalGeneratorService,
            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
        this.decimalGeneratorService = decimalGeneratorService;
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper =
                configMappersProvider.configMappers().get(getType().getName());
        DecimalGeneratorConfig mappedConfig = (DecimalGeneratorConfig) configMapper.map(config);
        return apply(
                transformToString(decimalGeneratorService.generate(mappedConfig)),
                converters,
                config.getRandomGenerator());
    }

    @Override
    public CustomType getType() {
        return CustomType.DECIMAL;
    }

    private List<String> transformToString(List<BigDecimal> values) {
        return values.stream()
                .map(decimal -> {
                    if (decimal == null) {
                        return null;
                    } else {
                        // BigDecimal.toString() может вернуть экспоненциальную запись (1E-7)
                        return decimal.toPlainString();
                    }
                })
                .toList();
    }
}
