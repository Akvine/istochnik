package ru.akvine.istochnik.controllers.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.akvine.istochnik.config.ratelimiter.GenerationRateLimiterProperties;
import ru.akvine.istochnik.exceptions.RateLimitExceededException;
import ru.akvine.istochnik.services.ratelimiter.UserRateLimiters;

/**
 * Ограничивает частоту запросов на генерацию для каждого пользователя отдельно.
 * Пользователь - это субъект JWT, если запрос аутентифицирован, иначе адрес клиента.
 * Проверка выполняется до чтения тела запроса
 */
@RequiredArgsConstructor
public class GenerationRateLimiterInterceptor implements HandlerInterceptor {
    private static final String USER_PREFIX = "user:";
    private static final String ADDRESS_PREFIX = "address:";

    private final UserRateLimiters userRateLimiters;
    private final GenerationRateLimiterProperties rateLimiterProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!userRateLimiters.acquirePermission(resolveUser(request))) {
            throw new RateLimitExceededException(
                    rateLimiterProperties.getLimitForPeriod(), rateLimiterProperties.getRefreshPeriodSeconds());
        }

        return true;
    }

    private String resolveUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return USER_PREFIX + authentication.getName();
        }

        return ADDRESS_PREFIX + request.getRemoteAddr();
    }
}
