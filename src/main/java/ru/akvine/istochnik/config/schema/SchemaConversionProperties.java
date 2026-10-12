package ru.akvine.istochnik.config.schema;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "schema.conversion")
public class SchemaConversionProperties {
    /**
     * Доступна ли конвертация схем - построение запросов на генерацию по SQL-скрипту или журналу изменений Liquibase
     */
    private boolean enabled = true;

    /**
     * Максимальный размер файла схемы в килобайтах
     */
    private int maxFileSizeKb = 512;
}
