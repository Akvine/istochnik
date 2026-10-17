package ru.akvine.istochnik.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.akvine.istochnik.config.ratelimiter.GenerationRateLimiterProperties;
import ru.akvine.istochnik.controllers.interceptors.GenerationRateLimiterInterceptor;
import ru.akvine.istochnik.services.ratelimiter.UserRateLimiters;

@Configuration
@RequiredArgsConstructor
public class GenerationRateLimiterConfig implements WebMvcConfigurer {
    private static final String GENERATOR_PATH = "/v1/generator";

    private final GenerationRateLimiterProperties rateLimiterProperties;

    @Bean
    public UserRateLimiters userRateLimiters() {
        return new UserRateLimiters(rateLimiterProperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (rateLimiterProperties.isEnabled()) {
            registry.addInterceptor(new GenerationRateLimiterInterceptor(userRateLimiters(), rateLimiterProperties))
                    .addPathPatterns(GENERATOR_PATH);
        }
    }
}
