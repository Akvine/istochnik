package ru.akvine.istochnik.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String BEARER_SCHEME_NAME = "bearer-jwt";

    @Value("${security.enabled:true}")
    private boolean securityEnabled;

    @Bean
    public OpenAPI openApi() {
        OpenAPI openApi = new OpenAPI()
                .info(new Info()
                        .title("Istochnik API")
                        .version("v1")
                        .description("Сервис генерации тестовых табличных данных. По описанию колонок возвращает файл "
                                + "в формате CSV, XLSX или SQL. Справочные методы возвращают доступные типы, "
                                + "стратегии, конвертеры и топики."));

        // Схема авторизации описывается только тогда, когда сервис действительно требует JWT
        if (securityEnabled) {
            openApi.components(new Components()
                            .addSecuritySchemes(
                                    BEARER_SCHEME_NAME,
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")))
                    .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME));
        }

        return openApi;
    }
}
