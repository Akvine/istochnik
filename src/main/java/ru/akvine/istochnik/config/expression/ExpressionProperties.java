package ru.akvine.istochnik.config.expression;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "generation.expression")
public class ExpressionProperties {
    /**
     * Доступна ли стратегия expression - колонки, значения которых вычисляются по другим колонкам той же строки
     */
    private boolean enabled = true;

    /**
     * Максимальная длина выражения в символах
     */
    private int maxLength = 1000;

    /**
     * Максимальная длина строки, которую выражение может вернуть для одной ячейки
     */
    private int maxResultLength = 1000;
}
