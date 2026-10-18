package ru.akvine.istochnik.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;

@Configuration
public class JacksonConfig {

    /**
     * Запрос на генерацию сервис не только принимает, но и возвращает - как результат конвертации схемы.
     * Его классы лежат в общей библиотеке, поэтому правила вывода заданы снаружи: поля со значениями
     * по умолчанию не выводятся, и ответ содержит только то, что задано для колонки. На разбор входящих
     * запросов это не влияет.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer generateTableRequestSerializationCustomizer() {
        return builder -> builder.mixIn(GenerateTableRequest.class, GenerateTableRequestMixin.class)
                .mixIn(ColumnDto.class, OnlyChangedFieldsMixin.class)
                .mixIn(ConfigDto.class, OnlyChangedFieldsMixin.class);
    }

    @JsonPropertyOrder({"tableName", "size", "fileType", "columns"})
    private interface GenerateTableRequestMixin {}

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private interface OnlyChangedFieldsMixin {}
}
