package ru.akvine.istochnik.controllers.v1.meta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.dto.converters.ConvertersListResponse;

@Tag(name = "Конвертеры", description = "Справочник преобразований над сгенерированными значениями")
@RequestMapping(value = "/v1/converters")
public interface ConvertersControllerMeta {
    @Operation(
            summary = "Получить конвертеры",
            description = "Возвращает для каждого базового типа имена доступных конвертеров. Конвертеры "
                    + "перечисляются в поле converters колонки и применяются по порядку. К пользовательскому типу "
                    + "применимы конвертеры его базового типа")
    @ApiResponse(
            responseCode = "200",
            description = "Конвертеры, сгруппированные по базовым типам",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConvertersListResponse.class)))
    @GetMapping
    Response list();
}
