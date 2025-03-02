package ru.akvine.istochnik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.managers.DateRangeServicesManager;
import ru.akvine.istochnik.managers.DateTimeRangeServicesManager;
import ru.akvine.istochnik.managers.FileTableGeneratorsManager;
import ru.akvine.istochnik.managers.TimeRangeServicesManager;
import ru.akvine.istochnik.managers.filters.DoubleFiltersManager;
import ru.akvine.istochnik.managers.filters.IntegerFiltersManager;
import ru.akvine.istochnik.managers.filters.StringFiltersManager;
import ru.akvine.istochnik.services.file.FileTableGenerator;
import ru.akvine.istochnik.services.filters.doubles.DoubleFilter;
import ru.akvine.istochnik.services.filters.integer.IntegerFilter;
import ru.akvine.istochnik.services.filters.string.StringFilter;
import ru.akvine.istochnik.services.generators.date.shift.AbstractDateRangeService;
import ru.akvine.istochnik.services.generators.datetime.shift.AbstractDateTimeRangeService;
import ru.akvine.istochnik.services.generators.time.shift.AbstractTimeRangeService;

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
        Map<String, IntegerFilter<Integer, Double>> filters = integerFilters
                .stream()
                .collect(toMap(IntegerFilter::getName, identity()));
        return new IntegerFiltersManager(filters);
    }

    @Bean
    public DoubleFiltersManager doubleFiltersManager(List<DoubleFilter<Double, Double>> doubleFilters) {
        Map<String, DoubleFilter<Double, Double>> filters = doubleFilters
                .stream()
                .collect(toMap(DoubleFilter::getName, identity()));
        return new DoubleFiltersManager(filters);
    }

    @Bean
    public StringFiltersManager stringFiltersManager(List<StringFilter<String, String>> stringFilters) {
        Map<String, StringFilter<String, String>> filters = stringFilters
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
}
