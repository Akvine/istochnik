package ru.akvine.istochnik.exceptions;

public class WorkbookMappingException extends RuntimeException {
    public WorkbookMappingException(Exception exception) {
        super(exception);
    }
}
