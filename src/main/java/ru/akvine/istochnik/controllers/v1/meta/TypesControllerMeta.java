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
import ru.akvine.compozit.commons.istochnik.BaseTypesListResponse;
import ru.akvine.compozit.commons.istochnik.CustomTypesListResponse;

@Tag(name = "Типы данных", description = "Справочник типов для стратегии algorithm")
@RequestMapping(value = "/v1/types")
public interface TypesControllerMeta {
    @Operation(
            summary = "Получить пользовательские типы",
            description = "Возвращает типы, построенные на базовых: даты и время, UUID, точные десятичные числа, "
                    + "реквизиты, номера карт и другие. Для каждого типа указаны базовый тип, описание и пример "
                    + "значения. Имя типа передаётся в поле type колонки")
    @ApiResponse(
            responseCode = "200",
            description = "Список пользовательских типов",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomTypesListResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Язык не поддерживается",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/custom")
    Response listCustom(
            @Parameter(description = "Язык описаний: en или ru", example = "ru")
                    @RequestParam(required = false, defaultValue = "en")
                    String lang);

    @Operation(
            summary = "Получить базовые типы",
            description = "Возвращает базовые типы: целые числа, числа с плавающей точкой, строки и логические "
                    + "значения. От базового типа зависит набор конвертеров и то, как значение попадает в файл")
    @ApiResponse(
            responseCode = "200",
            description = "Список базовых типов",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseTypesListResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Язык не поддерживается",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/base")
    Response listBase(
            @Parameter(description = "Язык описаний: en или ru", example = "ru")
                    @RequestParam(required = false, defaultValue = "en")
                    String lang);
}
