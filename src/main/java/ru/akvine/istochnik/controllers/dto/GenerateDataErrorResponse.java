package ru.akvine.istochnik.controllers.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.dto.ErrorResponse;

@Data
@Accessors(chain = true)
public class GenerateDataErrorResponse extends ErrorResponse {
    private final String generalInfoErrors;
    private final Map<String, List<String>> columnErrors;

    public GenerateDataErrorResponse(
            String code, String message, String generalInfoErrors, Map<String, List<String>> columnErrors) {
        super(code, message);
        this.generalInfoErrors = generalInfoErrors;
        this.columnErrors = columnErrors;
    }
}
