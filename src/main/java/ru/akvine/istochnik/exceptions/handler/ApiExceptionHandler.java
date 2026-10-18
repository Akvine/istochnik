package ru.akvine.istochnik.exceptions.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.akvine.istochnik.constants.ErrorConstants;
import ru.akvine.istochnik.controllers.ErrorResponse;
import ru.akvine.istochnik.controllers.dto.GenerateDataErrorResponse;
import ru.akvine.istochnik.exceptions.GenerationException;
import ru.akvine.istochnik.exceptions.LanguageNotSupportedException;
import ru.akvine.istochnik.exceptions.RateLimitExceededException;
import ru.akvine.istochnik.exceptions.SchemaConversionDisabledException;
import ru.akvine.istochnik.exceptions.SchemaConversionException;
import ru.akvine.istochnik.exceptions.validation.ConfigValidationException;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ConfigValidationException.class})
    public ResponseEntity<?> handleConfigValidationException(ConfigValidationException exception) {
        ErrorResponse errorResponse = new GenerateDataErrorResponse(
                ErrorConstants.Validation.REQUEST_FIELDS_INVALID_ERROR,
                "Request fields have invalid states",
                exception.getGeneralInfo(),
                exception.getValidationColumnsInfo().getColumnNamesPerErrorMessages());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({GenerationException.class})
    public ResponseEntity<?> handleGenerationException(GenerationException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorConstants.GENERATION_ERROR, exception.getMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RateLimitExceededException.class})
    public ResponseEntity<?> handleRateLimitExceededException(RateLimitExceededException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorConstants.RATE_LIMIT_EXCEEDED_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(exception.getRetryAfterSeconds()));
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler({SchemaConversionException.class})
    public ResponseEntity<?> handleSchemaConversionException(SchemaConversionException exception) {
        ErrorResponse errorResponse =
                new ErrorResponse(ErrorConstants.SCHEMA_CONVERSION_ERROR, exception.getMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SchemaConversionDisabledException.class})
    public ResponseEntity<?> handleSchemaConversionDisabledException(SchemaConversionDisabledException exception) {
        ErrorResponse errorResponse =
                new ErrorResponse(ErrorConstants.SCHEMA_CONVERSION_DISABLED_ERROR, exception.getMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({LanguageNotSupportedException.class})
    public ResponseEntity<?> handleLanguageNotSupportedException(LanguageNotSupportedException exception) {
        ErrorResponse errorResponse =
                new ErrorResponse(ErrorConstants.LANGUAGE_NOT_SUPPORTED_BY_APP, exception.getMessage());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
