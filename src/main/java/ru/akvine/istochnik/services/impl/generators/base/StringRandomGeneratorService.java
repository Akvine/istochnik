package ru.akvine.istochnik.services.impl.generators.base;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.BaseType;
import ru.akvine.istochnik.providers.ConfigMapperServicesProvider;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;

@Service
public class StringRandomGeneratorService extends AbstractBaseTypeGeneratorService {
    private final Random random = new SecureRandom();

    @Autowired
    protected StringRandomGeneratorService(
            ConfigMapperServicesProvider configMappersProvider,
            ConverterConvertersProvider converterConvertersProvider) {
        super(configMappersProvider, converterConvertersProvider);
    }

    @Override
    public List<?> generate(Config config, List<Converter> converters) {
        int count = 0;

        List<String> generatedValues = new ArrayList<>();
        while (count != config.getSize()) {
            if (config.isNotNull()) {
                String random = RandomStringUtils.secure().next(Integer.parseInt(config.getEnd()), true, true);
                generatedValues.add(random);
            } else {
                boolean isNull = random.nextBoolean();
                if (isNull) {
                    generatedValues.add(null);
                } else {
                    String random = RandomStringUtils.secure().next(Integer.parseInt(config.getEnd()), true, true);
                    generatedValues.add(random);
                }
            }

            count++;
        }
        return generatedValues;
    }

    @Override
    public BaseType getType() {
        return BaseType.STRING;
    }
}
