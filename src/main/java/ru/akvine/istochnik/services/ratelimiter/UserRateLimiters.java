package ru.akvine.istochnik.services.ratelimiter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import ru.akvine.istochnik.config.ratelimiter.GenerationRateLimiterProperties;

/**
 * Ограничители частоты запросов на генерацию, по одному на пользователя.
 * Ограничитель создаётся при первом запросе пользователя и удаляется, когда пользователь дольше периода
 * не присылал запросов: к этому времени лимит восстановился бы полностью, и новый ограничитель ведёт себя
 * так же, как прежний. Число ограничителей не больше настройки max-users, чтобы память не росла
 * от множества разных клиентов
 */
public class UserRateLimiters {
    private static final String RATE_LIMITER_NAME = "generation";

    private final RateLimiterConfig rateLimiterConfig;
    private final Cache<String, RateLimiter> rateLimiters;

    public UserRateLimiters(GenerationRateLimiterProperties properties) {
        this(properties, Ticker.systemTicker());
    }

    UserRateLimiters(GenerationRateLimiterProperties properties, Ticker ticker) {
        Duration refreshPeriod = Duration.ofSeconds(properties.getRefreshPeriodSeconds());
        this.rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(properties.getLimitForPeriod())
                .limitRefreshPeriod(refreshPeriod)
                // Запрос сверх лимита отклоняется сразу, а не ждёт освобождения места
                .timeoutDuration(Duration.ZERO)
                .build();
        this.rateLimiters = Caffeine.newBuilder()
                .expireAfterAccess(refreshPeriod)
                .maximumSize(properties.getMaxUsers())
                .ticker(ticker)
                .build();
    }

    /**
     * @return true, если лимит пользователя ещё не исчерпан, и запрос можно выполнить
     */
    public boolean acquirePermission(String user) {
        return rateLimiters
                .get(user, key -> RateLimiter.of(RATE_LIMITER_NAME, rateLimiterConfig))
                .acquirePermission();
    }
}
