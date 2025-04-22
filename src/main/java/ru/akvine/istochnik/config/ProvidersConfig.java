package ru.akvine.istochnik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.enums.*;
import ru.akvine.istochnik.providers.*;
import ru.akvine.istochnik.providers.filters.DoubleFiltersProvider;
import ru.akvine.istochnik.providers.filters.FilterServicesProvider;
import ru.akvine.istochnik.providers.filters.IntegerFiltersProvider;
import ru.akvine.istochnik.providers.filters.StringFiltersProvider;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;
import ru.akvine.istochnik.services.FilterService;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.file.FileTableGenerator;
import ru.akvine.istochnik.services.file.excel.factory.CellConfigurer;
import ru.akvine.istochnik.services.filters.doubles.DoubleFilter;
import ru.akvine.istochnik.services.filters.integer.IntegerFilter;
import ru.akvine.istochnik.services.filters.string.StringFilter;
import ru.akvine.istochnik.services.generators.Config;
import ru.akvine.istochnik.services.generators.date.shift.AbstractDateRangeService;
import ru.akvine.istochnik.services.generators.datetime.shift.AbstractDateTimeRangeService;
import ru.akvine.istochnik.services.generators.time.shift.AbstractTimeRangeService;
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
public class ProvidersConfig {

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
    public IntegerFiltersProvider integerFiltersProvider(List<IntegerFilter<Integer, Double>> integerFilters) {
        Map<FilterType, IntegerFilter<Integer, Double>> filters = integerFilters
                .stream()
                .collect(toMap(IntegerFilter::getName, identity()));
        return new IntegerFiltersProvider(filters);
    }

    @Bean
    public DoubleFiltersProvider doubleFiltersProvider(List<DoubleFilter<Double, Double>> doubleFilters) {
        Map<FilterType, DoubleFilter<Double, Double>> filters = doubleFilters
                .stream()
                .collect(toMap(DoubleFilter::getName, identity()));
        return new DoubleFiltersProvider(filters);
    }

    @Bean
    public StringFiltersProvider stringFiltersProvider(List<StringFilter<String, String>> stringFilters) {
        Map<FilterType, StringFilter<String, String>> filters = stringFilters
                .stream()
                .collect(toMap(StringFilter::getName, identity()));
        return new StringFiltersProvider(filters);
    }

    @Bean
    public FileTableGeneratorsProvider fileTableServicesProvider(List<FileTableGenerator> generators) {
        Map<FileType, FileTableGenerator> filters = generators
                .stream()
                .collect(toMap(FileTableGenerator::getType, identity()));
        return new FileTableGeneratorsProvider(filters);
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
    public FilterServicesProvider filterServicesProvider(List<FilterService> filterServices) {
        Map<BaseType, FilterService> filterServicesMap = filterServices
                .stream()
                .collect(toMap(FilterService::getType, identity()));
        return new FilterServicesProvider(filterServicesMap);
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
}
