package ru.akvine.istochnik.services.impl.generators.custom;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.CustomType;
import ru.akvine.istochnik.managers.ConfigMapperServicesManager;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Filter;
import ru.akvine.istochnik.services.generators.ogrn.OgrnGeneratorConfig;
import ru.akvine.istochnik.services.generators.ogrn.OgrnRandomGenerator;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;

import java.util.List;

@Service
public class OgrnRandomGeneratorService extends AbstractCustomTypeGeneratorService {
    private final OgrnRandomGenerator ogrnRandomGenerator;

    protected OgrnRandomGeneratorService(ConfigMapperServicesManager configMappersManager,
                                         OgrnRandomGenerator ogrnRandomGenerator) {
        super(configMappersManager);
        this.ogrnRandomGenerator = ogrnRandomGenerator;
    }

    @Override
    public List<?> generate(Config config, List<Filter> filters) {
        ConfigMapperService<? extends ru.akvine.istochnik.services.generators.Config> configMapper = configMappersManager
                .configMappers()
                .get(getType().getName());
        OgrnGeneratorConfig mappedConfig = (OgrnGeneratorConfig) configMapper.map(config);
        return (List<?>) ogrnRandomGenerator.generate(mappedConfig);
    }

    @Override
    public CustomType getType() {
        return CustomType.OGRN;
    }
}
