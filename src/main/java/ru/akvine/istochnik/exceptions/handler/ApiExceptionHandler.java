package ru.akvine.istochnik.exceptions.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.akvine.istochnik.constants.ErrorConstants;
import ru.akvine.istochnik.controllers.dto.GenerateDataErrorResponse;
import ru.akvine.istochnik.controllers.dto.common.ErrorResponse;
import ru.akvine.istochnik.exceptions.validation.ConfigValidationException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ConfigValidationException.class})
    public ResponseEntity<?> handleConfigValidationException(ConfigValidationException exception) {
        ErrorResponse errorResponse = new GenerateDataErrorResponse(
                ErrorConstants.Validation.REQUEST_FIELDS_INVALID_ERROR,
                "Request fields have invalid states",
                exception.getGeneralInfo(),
                exception.getValidationColumnsInfo().getColumnNamesPerErrorMessages()
        );
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
