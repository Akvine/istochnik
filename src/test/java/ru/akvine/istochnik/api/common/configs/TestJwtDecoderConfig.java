package ru.akvine.istochnik.api.common.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Заменяет поставщика OAuth 2.0 в тестах: действителен любой токен, который начинается с {@link #VALID_TOKEN_PREFIX},
 * а субъект токена - сам токен. Так тесты могут представляться разными пользователями.
 * Конфигурация одна на все тесты: вложенные в тестовые классы конфигурации попадают и в контексты других тестов,
 * и одноимённые бины в них конфликтовали бы
 */
@TestConfiguration
public class TestJwtDecoderConfig {
    public static final String VALID_TOKEN_PREFIX = "valid";

    @Bean
    JwtDecoder jwtDecoder() {
        return token -> {
            if (!token.startsWith(VALID_TOKEN_PREFIX)) {
                throw new BadJwtException("Token is not valid");
            }

            return Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .subject(token)
                    .build();
        };
    }
}
