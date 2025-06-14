package ru.akvine.istochnik.exceptions;

import lombok.Getter;
import ru.akvine.compozit.commons.istochnik.ErrorResolveInfo;

@Getter
public class DefaultException extends RuntimeException {
    private final ErrorResolveInfo errorResolveInfo;

    public DefaultException(String message ,ErrorResolveInfo errorResolveInfo) {
        super(message);
        this.errorResolveInfo = errorResolveInfo;
    }
}
