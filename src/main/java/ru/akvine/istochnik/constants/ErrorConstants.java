package ru.akvine.istochnik.constants;

public final class ErrorConstants {
    private ErrorConstants() throws IllegalAccessException {
        throw new IllegalAccessException("Calling " + ErrorConstants.class.getSimpleName() + " is prohibited!");
    }

    public interface Validation {
        String TYPE_INVALID_ERROR = "type.invalid.error";
    }
}
