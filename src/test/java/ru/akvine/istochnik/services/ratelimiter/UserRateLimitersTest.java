package ru.akvine.istochnik.services.ratelimiter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.akvine.istochnik.config.ratelimiter.GenerationRateLimiterProperties;

@DisplayName("User rate limiters tests")
class UserRateLimitersTest {
    private static final int PERIOD_SECONDS = 60;

    // Время, по которому кэш решает, что ограничитель пользователя больше не нужен
    private final AtomicLong nanos = new AtomicLong();
    private final UserRateLimiters rateLimiters = new UserRateLimiters(properties(), nanos::get);

    @Test
    @DisplayName("Every user has his own limit")
    void users_have_separate_limits() {
        assertThat(rateLimiters.acquirePermission("alice")).isTrue();
        assertThat(rateLimiters.acquirePermission("alice")).isTrue();
        assertThat(rateLimiters.acquirePermission("alice")).isFalse();

        assertThat(rateLimiters.acquirePermission("bob")).isTrue();
        assertThat(rateLimiters.acquirePermission("bob")).isTrue();
        assertThat(rateLimiters.acquirePermission("bob")).isFalse();
    }

    @Test
    @DisplayName("User without requests longer than the period gets the full limit again")
    void idle_user_gets_full_limit() {
        rateLimiters.acquirePermission("alice");
        rateLimiters.acquirePermission("alice");

        // Половина периода - ограничитель ещё действует, и лимит исчерпан
        nanos.addAndGet(TimeUnit.SECONDS.toNanos(PERIOD_SECONDS / 2));
        assertThat(rateLimiters.acquirePermission("alice")).isFalse();

        // Запросов не было дольше периода - ограничитель удалён, лимит снова полный
        nanos.addAndGet(TimeUnit.SECONDS.toNanos(PERIOD_SECONDS + 1));
        assertThat(rateLimiters.acquirePermission("alice")).isTrue();
        assertThat(rateLimiters.acquirePermission("alice")).isTrue();
        assertThat(rateLimiters.acquirePermission("alice")).isFalse();
    }

    private static GenerationRateLimiterProperties properties() {
        GenerationRateLimiterProperties properties = new GenerationRateLimiterProperties();
        properties.setLimitForPeriod(2);
        properties.setRefreshPeriodSeconds(PERIOD_SECONDS);
        properties.setMaxUsers(100);
        return properties;
    }
}
