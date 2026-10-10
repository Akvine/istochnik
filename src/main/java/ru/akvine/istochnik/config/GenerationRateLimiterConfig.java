package ru.akvine.istochnik.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.akvine.istochnik.config.ratelimiter.GenerationRateLimiterProperties;
import ru.akvine.istochnik.controllers.interceptors.GenerationRateLimiterInterceptor;

@Configuration
@RequiredArgsConstructor
public class GenerationRateLimiterConfig implements WebMvcConfigurer {
    private static final String GENERATION_RATE_LIMITER_NAME = "generation";
    private static final String GENERATOR_PATH = "/v1/generator";

    private final GenerationRateLimiterProperties rateLimiterProperties;

    @Bean
    public RateLimiter generationRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(rateLimiterProperties.getLimitForPeriod())
                .limitRefreshPeriod(Duration.ofSeconds(rateLimiterProperties.getRefreshPeriodSeconds()))
                // Запрос сверх лимита отклоняется сразу, а не ждёт освобождения места
                .timeoutDuration(Duration.ZERO)
                .build();
        return RateLimiter.of(GENERATION_RATE_LIMITER_NAME, config);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (rateLimiterProperties.isEnabled()) {
            registry.addInterceptor(new GenerationRateLimiterInterceptor(generationRateLimiter(), rateLimiterProperties))
                    .addPathPatterns(GENERATOR_PATH);
        }
    }
}
