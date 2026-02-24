package ru.akvine.istochnik.exceptions;

import lombok.Getter;

@Getter
public class UUIDGenerationException extends RuntimeException {
    private final String message;

    public UUIDGenerationException(String message, Exception cause) {
        super(cause);
        this.message = message;
    }
}
