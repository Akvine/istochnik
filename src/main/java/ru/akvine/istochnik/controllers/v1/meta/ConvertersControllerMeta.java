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
import ru.akvine.istochnik.controllers.ErrorResponse;
import ru.akvine.istochnik.controllers.dto.Response;
import ru.akvine.istochnik.controllers.dto.converters.ConvertersListResponse;

@Tag(name = "Конвертеры", description = "Справочник преобразований над сгенерированными значениями")
@RequestMapping(value = "/v1/converters")
public interface ConvertersControllerMeta {
    @Operation(
            summary = "Получить конвертеры",
            description = "Возвращает для каждого базового типа доступные конвертеры: имя, описание и пример "
                    + "применения - аргументы, значения колонки до конвертера и после него. Если конвертер "
                    + "работает случайно, в примере один из возможных результатов. Конвертеры "
                    + "перечисляются в поле converters колонки и применяются по порядку. К пользовательскому типу "
                    + "применимы конвертеры его базового типа")
    @ApiResponse(
            responseCode = "200",
            description = "Конвертеры, сгруппированные по базовым типам",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConvertersListResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Язык не поддерживается",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    Response list(
            @Parameter(description = "Язык описаний: en или ru", example = "ru")
                    @RequestParam(required = false, defaultValue = "en")
                    String lang);
}
