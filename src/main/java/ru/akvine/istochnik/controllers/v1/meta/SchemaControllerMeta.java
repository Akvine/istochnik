package ru.akvine.istochnik.controllers.v1.meta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.akvine.compozit.commons.dto.ErrorResponse;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.dto.schema.ConvertSchemaResponse;

@Tag(
        name = "Конвертация схем",
        description = "Построение запросов на генерацию по схеме базы данных: SQL-скрипту или журналу изменений "
                + "Liquibase")
@RequestMapping(value = "/v1/schemas")
public interface SchemaControllerMeta {
    String DEFAULT_SIZE = "100";
    String DEFAULT_FILE_TYPE = "sql";

    String RESPONSE_EXAMPLE =
            """
            {
              "status": "SUCCESS",
              "requests": [
                {
                  "tableName": "users",
                  "size": 100,
                  "fileType": "sql",
                  "columns": [
                    {
                      "name": "id",
                      "type": "int",
                      "generationStrategy": "algorithm",
                      "config": { "notNull": true, "rangeType": "shift", "start": "1", "step": "1" }
                    },
                    {
                      "name": "email",
                      "type": "str",
                      "generationStrategy": "algorithm",
                      "config": { "unique": true, "notNull": true, "length": 16 }
                    },
                    {
                      "name": "role_id",
                      "type": "int",
                      "generationStrategy": "algorithm",
                      "config": { "rangeType": "random", "start": "1", "end": "101" }
                    }
                  ]
                }
              ],
              "count": 1,
              "warnings": [
                "Table [users], column [role_id]: references [roles(id)], values are generated in range [1, 100] in expectation of sequential ids of the referenced table"
              ]
            }
            """;

    @Operation(
            summary = "Построить запросы на генерацию по схеме",
            description = "Читает из файла миграции описания таблиц и для каждой возвращает готовый запрос к "
                    + "POST /v1/generator: стратегия, тип и диапазон значений каждой колонки подобраны по её типу "
                    + "и ограничениям в схеме. Поддерживаются SQL-скрипты (CREATE TABLE и последующие ALTER TABLE, "
                    + "CREATE UNIQUE INDEX, CREATE TYPE ... AS ENUM) и журналы изменений Liquibase в формате XML "
                    + "(createTable и последующие изменения колонок и ключей). Запросы - это заготовки: диапазоны "
                    + "значений условны, их стоит уточнить перед генерацией. Метод отключается настройкой "
                    + "schema.conversion.enabled.")
    @ApiResponse(
            responseCode = "200",
            description = "Запросы на генерацию - по одному на таблицу, в порядке создания таблиц в схеме, - и "
                    + "предупреждения о том, что в схеме учтено неточно: неподдерживаемые типы, внешние и составные "
                    + "ключи, пропущенные колонки и таблицы",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConvertSchemaResponse.class),
                            examples = @ExampleObject(name = "Таблица users", value = RESPONSE_EXAMPLE)))
    @ApiResponse(
            responseCode = "400",
            description = "Файл не передан или слишком велик, неверные параметры, схема не разобрана "
                    + "или в ней нет таблиц",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Конвертация схем отключена настройкой schema.conversion.enabled",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/convert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Response convert(
            @Parameter(description = "Файл схемы в кодировке UTF-8: SQL-скрипт или журнал изменений Liquibase (XML)")
                    @RequestPart(value = "file", required = false)
                    MultipartFile file,
            @Parameter(
                            description = "Формат схемы: sql или xml. Если не задан, определяется по расширению файла",
                            example = "sql")
                    @RequestParam(value = "type", required = false)
                    String type,
            @Parameter(description = "Количество строк в каждом запросе на генерацию", example = "100")
                    @RequestParam(value = "size", required = false, defaultValue = DEFAULT_SIZE)
                    int size,
            @Parameter(description = "Формат файла в каждом запросе на генерацию: csv, xlsx или sql", example = "sql")
                    @RequestParam(value = "fileType", required = false, defaultValue = DEFAULT_FILE_TYPE)
                    String fileType);
}
