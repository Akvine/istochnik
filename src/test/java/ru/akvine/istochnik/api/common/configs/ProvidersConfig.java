package ru.akvine.istochnik.api.common.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.akvine.istochnik.api.common.converters.TypeConverter;
import ru.akvine.istochnik.api.common.detectors.Detector;
import ru.akvine.istochnik.api.common.providers.DetectorsProvider;
import ru.akvine.istochnik.api.common.providers.TypeConvertersProvider;
import ru.akvine.istochnik.enums.BaseType;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
@Profile("test")
public class ProvidersConfig {

    @Bean
    public TypeConvertersProvider typeConvertersProvider(List<TypeConverter<?>> typeConverters) {
        Map<BaseType, TypeConverter<?>> converters = typeConverters
                .stream()
                .collect(toMap(TypeConverter::getByType, identity()));
        return new TypeConvertersProvider(converters);
    }

    @Bean
    public DetectorsProvider detectorsProvider(List<Detector<?>> detectors) {
        Map<BaseType, Detector<?>> detectorsList = detectors
                .stream()
                .collect(toMap(Detector::getByType, identity()));
        return new DetectorsProvider(detectorsList);
    }
}
