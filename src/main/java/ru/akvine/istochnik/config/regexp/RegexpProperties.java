package ru.akvine.istochnik.config.regexp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "generation.regexp")
public class RegexpProperties {
    /**
     * Максимальное число регулярных выражений в одной колонке
     */
    private int maxCount = 10;

    /**
     * Максимальная длина регулярного выражения в символах
     */
    private int maxLength = 1000;

    /**
     * Максимальная длина строки, которую задаёт выражение. Повторы без верхней границы (*, +) считаются
     * одним повтором: сколько их будет, решает случай
     */
    private int maxResultLength = 1000;

    /**
     * Сколько состояний может быть у автомата, который строится по выражению для генерации строк
     */
    private int maxAutomatonStates = 2000;
}
