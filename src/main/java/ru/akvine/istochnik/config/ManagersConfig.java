package ru.akvine.istochnik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.enums.DateShiftType;
import ru.akvine.istochnik.managers.LocalDateTimeShiftServicesManager;
import ru.akvine.istochnik.services.generators.date.localdatetime.shift.AbstractLocalDateTimeShiftService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
public class ManagersConfig {

    @Bean
    public LocalDateTimeShiftServicesManager localDateTimeShiftServicesManager(Collection<AbstractLocalDateTimeShiftService<LocalDateTime, Long>> services) {
        Map<DateShiftType, AbstractLocalDateTimeShiftService<LocalDateTime, Long>> localDateTimeShiftServices = services
                .stream()
                .collect(toMap(AbstractLocalDateTimeShiftService::getByType, identity()));
        return new LocalDateTimeShiftServicesManager(localDateTimeShiftServices);
    }
}
