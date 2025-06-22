package ru.akvine.istochnik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.enums.*;
import ru.akvine.istochnik.providers.*;
import ru.akvine.istochnik.providers.converters.ConverterConvertersProvider;
import ru.akvine.istochnik.providers.converters.DoubleConvertersProvider;
import ru.akvine.istochnik.providers.converters.IntegerConvertersProvider;
import ru.akvine.istochnik.providers.converters.StringConvertersProvider;
import ru.akvine.istochnik.services.*;
import ru.akvine.istochnik.services.converters.doubles.DoubleConverter;
import ru.akvine.istochnik.services.converters.integer.IntegerConverter;
import ru.akvine.istochnik.services.converters.string.StringConverter;
import ru.akvine.istochnik.services.file.FileTableGenerator;
import ru.akvine.istochnik.services.file.excel.factory.CellConfigurer;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.custom.date.shift.AbstractDateRangeService;
import ru.akvine.istochnik.services.generators.custom.datetime.shift.AbstractDateTimeRangeService;
import ru.akvine.istochnik.services.generators.custom.time.shift.AbstractTimeRangeService;
import ru.akvine.istochnik.services.mappers.ConfigMapperService;
import ru.akvine.istochnik.validators.type.BaseTypeValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
/*
    Сделал переименование из ProvidersConfig в ProvidersDelegateConfig,
    чтобы не конфиликтовали имена классов ProvidersConfig из тестовых пакетов и и тут
 */
public class ProvidersDelegateConfig {

    @Bean
    public DateTimeRangeServicesProvider dateTimeShiftServicesProvider(Collection<AbstractDateTimeRangeService<LocalDateTime, Long>> services) {
        Map<DateShiftType, AbstractDateTimeRangeService<LocalDateTime, Long>> localDateTimeShiftServices = services
                .stream()
                .collect(toMap(AbstractDateTimeRangeService::getByType, identity()));
        return new DateTimeRangeServicesProvider(localDateTimeShiftServices);
    }

    @Bean
    public TimeRangeServicesProvider timeRangeServicesProvider(Collection<AbstractTimeRangeService<LocalTime, Integer>> services) {
        Map<DateShiftType, AbstractTimeRangeService<LocalTime, Integer>> timeShiftServices = services
                .stream()
                .collect(toMap(AbstractTimeRangeService::getByType, identity()));
        return new TimeRangeServicesProvider(timeShiftServices);
    }

    @Bean
    public DateRangeServicesProvider dateRangeServicesProvider(Collection<AbstractDateRangeService<LocalDate, Integer>> services) {
        Map<DateShiftType, AbstractDateRangeService<LocalDate, Integer>> localDateShiftServices = services
                .stream()
                .collect(toMap(AbstractDateRangeService::getByType, identity()));
        return new DateRangeServicesProvider(localDateShiftServices);
    }

    @Bean
    public IntegerConvertersProvider integerConvertersProvider(List<IntegerConverter<Long, Double>> integerConverters) {
        Map<ConverterType, IntegerConverter<Long, Double>> converters = integerConverters
                .stream()
                .collect(toMap(IntegerConverter::getName, identity()));
        return new IntegerConvertersProvider(converters);
    }

    @Bean
    public DoubleConvertersProvider doubleConvertersProvider(List<DoubleConverter<Double, Double>> doubleConverters) {
        Map<ConverterType, DoubleConverter<Double, Double>> converters = doubleConverters
                .stream()
                .collect(toMap(DoubleConverter::getName, identity()));
        return new DoubleConvertersProvider(converters);
    }

    @Bean
    public StringConvertersProvider stringConvertersProvider(List<StringConverter<String, String>> stringConverters) {
        Map<ConverterType, StringConverter<String, String>> converters = stringConverters
                .stream()
                .collect(toMap(StringConverter::getName, identity()));
        return new StringConvertersProvider(converters);
    }

    @Bean
    public FileTableGeneratorsProvider fileTableServicesProvider(List<FileTableGenerator> generators) {
        Map<FileType, FileTableGenerator> converters = generators
                .stream()
                .collect(toMap(FileTableGenerator::getType, identity()));
        return new FileTableGeneratorsProvider(converters);
    }

    @Bean
    public BaseTypeValidatorsProvider baseTypeValidatorsProvider(List<BaseTypeValidator> typeValidators) {
        Map<BaseType, BaseTypeValidator> validators = typeValidators
                .stream()
                .collect(toMap(BaseTypeValidator::getBaseType, identity()));
        return new BaseTypeValidatorsProvider(validators);
    }

    @Bean
    public ConfigMapperServicesProvider configMapperServicesProvider(List<ConfigMapperService<? extends Config>> configMapperServices) {
        Map<String, ConfigMapperService<? extends Config>> mappers = configMapperServices
                .stream()
                .collect(toMap(ConfigMapperService::getType, identity()));
        return new ConfigMapperServicesProvider(mappers);
    }

    @Bean
    public BaseTypeGeneratorServicesProvider baseTypeGeneratorServicesProvider(List<BaseTypeGeneratorService> baseTypeGeneratorServices) {
        Map<BaseType, BaseTypeGeneratorService> generatorServices = baseTypeGeneratorServices
                .stream()
                .collect(toMap(BaseTypeGeneratorService::getType, identity()));
        return new BaseTypeGeneratorServicesProvider(generatorServices);
    }

    @Bean
    public CustomTypeGeneratorServicesProvider customTypeGeneratorServicesProvider(List<CustomTypeGeneratorService> customTypeGeneratorServices) {
        Map<CustomType, CustomTypeGeneratorService> generatorServices = customTypeGeneratorServices
                .stream()
                .collect(toMap(CustomTypeGeneratorService::getType, identity()));
        return new CustomTypeGeneratorServicesProvider(generatorServices);
    }

    @Bean
    public ConverterConvertersProvider converterServicesProvider(List<ConverterService> converterServices) {
        Map<BaseType, ConverterService> converterServicesMap = converterServices
                .stream()
                .collect(toMap(ConverterService::getType, identity()));
        return new ConverterConvertersProvider(converterServicesMap);
    }

    @Bean
    public GenerationHandlersProvider generationHandlersProvider(List<GenerationHandler> generationHandlers) {
        Map<GenerationStrategy, GenerationHandler> handlers = generationHandlers
                .stream()
                .collect(toMap(GenerationHandler::getStrategy, identity()));
        return new GenerationHandlersProvider(handlers);
    }

    @Bean
    public CellConfigurersProvider cellFactoriesProvider(List<CellConfigurer> cellFactories) {
        Map<Class<?>, CellConfigurer> factories = cellFactories
                .stream()
                .collect(toMap(CellConfigurer::getType, identity()));
        return new CellConfigurersProvider(factories);
    }

    @Bean
    public FakerGeneratorServicesProvider fakerGeneratorServicesProvider(List<FakerGeneratorService> fakerGeneratorServices) {
        Map<Topic, FakerGeneratorService> generatorServices = fakerGeneratorServices
                .stream()
                .collect(toMap(FakerGeneratorService::getByTopic, identity()));
        return new FakerGeneratorServicesProvider(generatorServices);
    }
}
