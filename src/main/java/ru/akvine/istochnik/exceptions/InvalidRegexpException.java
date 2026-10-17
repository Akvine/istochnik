package ru.akvine.istochnik.exceptions;

public class InvalidRegexpException extends RuntimeException {
    public InvalidRegexpException(String message) {
        super(message);
    }
}
