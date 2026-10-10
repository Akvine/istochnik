package ru.akvine.istochnik.exceptions;

public class GenerationException extends RuntimeException {
    public GenerationException(Exception exception) {
        super(exception);
    }

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
