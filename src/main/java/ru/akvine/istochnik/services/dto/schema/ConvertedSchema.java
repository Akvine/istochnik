package ru.akvine.istochnik.services.dto.schema;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;

@Data
@Accessors(chain = true)
public class ConvertedSchema {
    /**
     * Запросы на генерацию - по одному на таблицу, в порядке создания таблиц в схеме
     */
    private List<GenerateTableRequest> requests;

    /**
     * Что в схеме учтено неточно или не учтено вовсе
     */
    private List<String> warnings;
}
