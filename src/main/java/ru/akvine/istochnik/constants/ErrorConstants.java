package ru.akvine.istochnik.constants;

public final class ErrorConstants {
    private ErrorConstants() throws IllegalAccessException {
        throw new IllegalAccessException("Calling " + ErrorConstants.class.getSimpleName() + " is prohibited!");
    }

    public static final String LANGUAGE_NOT_SUPPORTED_BY_APP = "language.notSupported.error";
    public static final String GENERATION_ERROR = "generation.error";
    public static final String RATE_LIMIT_EXCEEDED_ERROR = "rate.limit.exceeded.error";
    public static final String SCHEMA_CONVERSION_ERROR = "schema.conversion.error";
    public static final String SCHEMA_CONVERSION_DISABLED_ERROR = "schema.conversion.disabled.error";

    public interface Validation {
        String REQUEST_FIELDS_INVALID_ERROR = "request.fields.invalid.error";
        String TYPE_INVALID_ERROR = "type.invalid.error";
    }
}
