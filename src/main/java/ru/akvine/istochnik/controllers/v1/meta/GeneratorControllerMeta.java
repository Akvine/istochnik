package ru.akvine.istochnik.controllers.v1.meta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.akvine.istochnik.controllers.ErrorResponse;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.controllers.dto.GenerateDataErrorResponse;

@Tag(name = "Генерация", description = "Генерация файла с тестовыми данными по описанию колонок")
@RequestMapping(value = "/v1/generator")
public interface GeneratorControllerMeta {
    String ALGORITHM_AND_DICTIONARY_EXAMPLE =
            """
            {
              "size": 10,
              "fileType": "csv",
              "columns": [
                {
                  "name": "id",
                  "type": "int",
                  "generationStrategy": "algorithm",
                  "config": { "rangeType": "shift", "start": "1", "step": "1" }
                },
                {
                  "name": "status",
                  "type": "str",
                  "generationStrategy": "dictionary",
                  "config": {
                    "rangeType": "random",
                    "notNull": true,
                    "dictionaries": [["NEW", "ACTIVE", "BLOCKED"]],
                    "weights": { "NEW": 70, "ACTIVE": 25, "BLOCKED": 5 }
                  }
                },
                {
                  "name": "balance",
                  "type": "decimal",
                  "generationStrategy": "algorithm",
                  "config": { "rangeType": "random", "notNull": true, "start": "0.00", "end": "10000.00" }
                }
              ]
            }
            """;

    String SQL_EXAMPLE =
            """
            {
              "size": 5,
              "fileType": "sql",
              "tableName": "users",
              "columns": [
                {
                  "name": "id",
                  "type": "uuid",
                  "generationStrategy": "algorithm",
                  "config": {}
                },
                {
                  "name": "full_name",
                  "type": "str",
                  "generationStrategy": "faker",
                  "config": { "rangeType": "random", "notNull": true, "language": "ru", "topics": ["FULL_NAME"] }
                },
                {
                  "name": "created_at",
                  "type": "datetime",
                  "generationStrategy": "algorithm",
                  "config": {
                    "rangeType": "random",
                    "notNull": true,
                    "start": "2025-01-01 00:00:00",
                    "end": "2025-12-31 23:59:59"
                  },
                  "converters": [{ "name": "format", "arguments": ["yyyy-MM-dd HH:mm:ss"] }]
                }
              ]
            }
            """;

    String EXPRESSION_EXAMPLE =
            """
            {
              "size": 10,
              "fileType": "csv",
              "columns": [
                {
                  "name": "price",
                  "type": "decimal",
                  "generationStrategy": "algorithm",
                  "config": { "rangeType": "random", "notNull": true, "start": "1.00", "end": "500.00" }
                },
                {
                  "name": "qty",
                  "type": "int",
                  "generationStrategy": "algorithm",
                  "config": { "rangeType": "random", "notNull": true, "start": "1", "end": "10" }
                },
                {
                  "name": "total",
                  "type": "str",
                  "generationStrategy": "expression",
                  "config": { "expression": "#price * #qty" }
                },
                {
                  "name": "is_large",
                  "type": "bool",
                  "generationStrategy": "expression",
                  "config": { "expression": "#toDouble(#total) > 1000" }
                }
              ]
            }
            """;

    @Operation(
            summary = "Сгенерировать файл с данными",
            description = "Генерирует таблицу по описанию колонок и возвращает её файлом в формате CSV, XLSX или SQL. "
                    + "Порядок колонок в файле совпадает с порядком в запросе. Количество строк, колонок и ячеек "
                    + "ограничено настройками сервиса, частота запросов - лимитом (см. ответ 429). "
                    + "Справочные методы этого API возвращают допустимые типы, стратегии, конвертеры и топики.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Описание таблицы: количество строк, формат файла и список колонок. "
                    + "Для каждой колонки задаются стратегия генерации, тип и параметры",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GenerateTableRequest.class),
                            examples = {
                                @ExampleObject(
                                        name = "CSV: последовательность, словарь и десятичные числа",
                                        value = ALGORITHM_AND_DICTIONARY_EXAMPLE),
                                @ExampleObject(name = "SQL: UUID, ФИО и дата с форматированием", value = SQL_EXAMPLE),
                                @ExampleObject(
                                        name = "CSV: колонки, вычисляемые по другим колонкам строки",
                                        value = EXPRESSION_EXAMPLE)
                            }))
    @ApiResponse(
            responseCode = "200",
            description = "Сгенерированный файл. Имя файла передаётся в заголовке Content-Disposition",
            content = {
                @Content(mediaType = "application/csv", schema = @Schema(type = "string", format = "binary")),
                @Content(mediaType = "application/xlsx", schema = @Schema(type = "string", format = "binary")),
                @Content(mediaType = "application/sql", schema = @Schema(type = "string", format = "binary"))
            })
    @ApiResponse(
            responseCode = "400",
            description = "Ошибки в параметрах запроса: общие и по каждой колонке",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GenerateDataErrorResponse.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Превышен лимит запросов на генерацию для пользователя. Лимит у каждого пользователя свой. "
                    + "Заголовок Retry-After содержит время ожидания в секундах",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка во время генерации колонки, для которой задана политика INTERRUPT",
            content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    ResponseEntity<?> generate(
            @Parameter(description = "Имя возвращаемого файла без расширения", example = "users")
                    @RequestParam(value = "tableName", required = false, defaultValue = "response")
                    String tableName,
            @RequestBody @Valid GenerateTableRequest request);
}
