package ru.akvine.istochnik.controllers.v1.meta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.akvine.compozit.commons.dto.ErrorResponse;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.dto.strategies.StrategiesListResponse;

@Tag(name = "Стратегии генерации", description = "Справочник стратегий генерации колонок")
@RequestMapping(value = "/v1/strategies")
public interface GenerationStrategiesControllerMeta {
    @Operation(
            summary = "Получить стратегии генерации",
            description = "Возвращает стратегии constant, algorithm, dictionary, regexp и faker с описанием и "
                    + "признаками поддержки ограничений unique, notNull и valid. Стратегия expression (вычисляемые "
                    + "колонки) возвращается, только если включена в настройках сервиса. Имя стратегии передаётся "
                    + "в поле generationStrategy колонки")
    @ApiResponse(
            responseCode = "200",
            description = "Список стратегий",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StrategiesListResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Язык не поддерживается",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    Response list(
            @Parameter(description = "Язык описаний: en или ru", example = "ru")
                    @RequestParam(required = false, defaultValue = "en")
                    String lang);
}
