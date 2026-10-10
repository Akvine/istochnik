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
import ru.akvine.istochnik.controllers.dto.topics.ListTopicsResponse;

@Tag(name = "Топики", description = "Справочник тем для стратегии faker")
@RequestMapping(value = "/v1/topics")
public interface TopicsControllerMeta {
    @Operation(
            summary = "Получить топики",
            description = "Возвращает темы, по которым стратегия faker генерирует правдоподобные данные: имена, "
                    + "адреса, телефоны, email и другие. Для каждого топика указаны описание и пример значения. "
                    + "Имена топиков передаются в поле config.topics колонки")
    @ApiResponse(
            responseCode = "200",
            description = "Список топиков",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListTopicsResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Язык не поддерживается",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    Response list(
            @Parameter(description = "Язык описаний и примеров: en или ru", example = "ru")
                    @RequestParam(required = false, defaultValue = "en")
                    String lang);
}
