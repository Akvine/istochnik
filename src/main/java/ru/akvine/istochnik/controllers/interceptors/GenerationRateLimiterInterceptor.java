package ru.akvine.istochnik.controllers.interceptors;

import io.github.resilience4j.ratelimiter.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.akvine.istochnik.config.ratelimiter.GenerationRateLimiterProperties;
import ru.akvine.istochnik.exceptions.RateLimitExceededException;

/**
 * Ограничивает частоту запросов на генерацию. Лимит общий для всех клиентов экземпляра приложения.
 * Проверка выполняется до чтения тела запроса
 */
@RequiredArgsConstructor
public class GenerationRateLimiterInterceptor implements HandlerInterceptor {
    private final RateLimiter rateLimiter;
    private final GenerationRateLimiterProperties rateLimiterProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!rateLimiter.acquirePermission()) {
            throw new RateLimitExceededException(
                    rateLimiterProperties.getLimitForPeriod(), rateLimiterProperties.getRefreshPeriodSeconds());
        }

        return true;
    }
}
