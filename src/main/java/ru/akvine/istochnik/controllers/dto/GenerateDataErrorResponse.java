package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.compozit.commons.dto.ErrorResponse;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class GenerateDataErrorResponse extends ErrorResponse {
    private final String generalInfoErrors;
    private final Map<String, List<String>> columnErrors;

    public GenerateDataErrorResponse(String code,
                                     String message,
                                     String generalInfoErrors,
                                     Map<String, List<String>> columnErrors) {
        super(code, message);
        this.generalInfoErrors = generalInfoErrors;
        this.columnErrors = columnErrors;
    }
}
