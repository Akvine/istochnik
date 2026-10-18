package ru.akvine.istochnik.controllers.dto.schema;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.controllers.dto.SuccessfulResponse;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;

@Data
@Accessors(chain = true)
public class ConvertSchemaResponse extends SuccessfulResponse {
    /**
     * Запросы на генерацию - по одному на таблицу. Каждый можно без изменений отправить в метод генерации
     */
    private List<GenerateTableRequest> requests;

    private int count;

    /**
     * Что в схеме учтено неточно или не учтено вовсе
     */
    private List<String> warnings;
}
