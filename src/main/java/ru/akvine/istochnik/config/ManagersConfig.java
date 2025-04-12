package ru.akvine.istochnik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.enums.*;
import ru.akvine.istochnik.managers.*;
import ru.akvine.istochnik.managers.filters.DoubleFiltersManager;
import ru.akvine.istochnik.managers.filters.FilterServicesManager;
import ru.akvine.istochnik.managers.filters.IntegerFiltersManager;
import ru.akvine.istochnik.managers.filters.StringFiltersManager;
import ru.akvine.istochnik.services.BaseTypeGeneratorService;
import ru.akvine.istochnik.services.CustomTypeGeneratorService;
import ru.akvine.istochnik.services.FilterService;
import ru.akvine.istochnik.services.file.FileTableGenerator;
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
public class ManagersConfig {

    @Bean
    public DateTimeRangeServicesManager dateTimeShiftServicesManager(Collection<AbstractDateTimeRangeService<LocalDateTime, Long>> services) {
        Map<DateShiftType, AbstractDateTimeRangeService<LocalDateTime, Long>> localDateTimeShiftServices = services
                .stream()
                .collect(toMap(AbstractDateTimeRangeService::getByType, identity()));
        return new DateTimeRangeServicesManager(localDateTimeShiftServices);
    }

    @Bean
    public TimeRangeServicesManager timeRangeServicesManager(Collection<AbstractTimeRangeService<LocalTime, Integer>> services) {
        Map<DateShiftType, AbstractTimeRangeService<LocalTime, Integer>> timeShiftServices = services
                .stream()
                .collect(toMap(AbstractTimeRangeService::getByType, identity()));
        return new TimeRangeServicesManager(timeShiftServices);
    }

    @Bean
    public DateRangeServicesManager dateRangeServicesManager(Collection<AbstractDateRangeService<LocalDate, Integer>> services) {
        Map<DateShiftType, AbstractDateRangeService<LocalDate, Integer>> localDateShiftServices = services
                .stream()
                .collect(toMap(AbstractDateRangeService::getByType, identity()));
        return new DateRangeServicesManager(localDateShiftServices);
    }

    @Bean
    public IntegerFiltersManager integerFiltersManager(List<IntegerFilter<Integer, Double>> integerFilters) {
        Map<FilterType, IntegerFilter<Integer, Double>> filters = integerFilters
                .stream()
                .collect(toMap(IntegerFilter::getName, identity()));
        return new IntegerFiltersManager(filters);
    }

    @Bean
    public DoubleFiltersManager doubleFiltersManager(List<DoubleFilter<Double, Double>> doubleFilters) {
        Map<FilterType, DoubleFilter<Double, Double>> filters = doubleFilters
                .stream()
                .collect(toMap(DoubleFilter::getName, identity()));
        return new DoubleFiltersManager(filters);
    }

    @Bean
    public StringFiltersManager stringFiltersManager(List<StringFilter<String, String>> stringFilters) {
        Map<FilterType, StringFilter<String, String>> filters = stringFilters
                .stream()
                .collect(toMap(StringFilter::getName, identity()));
        return new StringFiltersManager(filters);
    }

    @Bean
    public FileTableGeneratorsManager fileTableServicesManager(List<FileTableGenerator> generators) {
        Map<FileType, FileTableGenerator> filters = generators
                .stream()
                .collect(toMap(FileTableGenerator::getType, identity()));
        return new FileTableGeneratorsManager(filters);
    }

    @Bean
    public BaseTypeValidatorsManager baseTypeValidatorsManager(List<BaseTypeValidator> typeValidators) {
        Map<BaseType, BaseTypeValidator> validators = typeValidators
                .stream()
                .collect(toMap(BaseTypeValidator::getBaseType, identity()));
        return new BaseTypeValidatorsManager(validators);
    }

    @Bean
    public ConfigMapperServicesManager configMapperServicesManager(List<ConfigMapperService<? extends Config>> configMapperServices) {
        Map<String, ConfigMapperService<? extends Config>> mappers = configMapperServices
                .stream()
                .collect(toMap(ConfigMapperService::getType, identity()));
        return new ConfigMapperServicesManager(mappers);
    }

    @Bean
    public BaseTypeGeneratorServicesManager baseTypeGeneratorServicesManager(List<BaseTypeGeneratorService> baseTypeGeneratorServices) {
        Map<BaseType, BaseTypeGeneratorService> generatorServices = baseTypeGeneratorServices
                .stream()
                .collect(toMap(BaseTypeGeneratorService::getType, identity()));
        return new BaseTypeGeneratorServicesManager(generatorServices);
    }

    @Bean
    public CustomTypeGeneratorServicesManager customTypeGeneratorServicesManager(List<CustomTypeGeneratorService> customTypeGeneratorServices) {
        Map<CustomType, CustomTypeGeneratorService> generatorServices = customTypeGeneratorServices
                .stream()
                .collect(toMap(CustomTypeGeneratorService::getType, identity()));
        return new CustomTypeGeneratorServicesManager(generatorServices);
    }

    @Bean
    public FilterServicesManager filterServicesManager(List<FilterService> filterServices) {
        Map<BaseType, FilterService> filterServicesMap = filterServices
                .stream()
                .collect(toMap(FilterService::getType, identity()));
        return new FilterServicesManager(filterServicesMap);
    }
}
