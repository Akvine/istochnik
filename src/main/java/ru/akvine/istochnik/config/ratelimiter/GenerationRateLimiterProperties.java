package ru.akvine.istochnik.config.ratelimiter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "generation.rate.limiter")
public class GenerationRateLimiterProperties {
    private boolean enabled = true;

    /**
     * Сколько запросов на генерацию принимается за один период
     */
    private int limitForPeriod = 20;

    /**
     * Длительность периода в секундах, по истечении которого лимит восстанавливается
     */
    private int refreshPeriodSeconds = 1;
}
