package ru.akvine.istochnik.exceptions;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
    private final int retryAfterSeconds;

    public RateLimitExceededException(int limitForPeriod, int refreshPeriodSeconds) {
        super(String.format(
                "Too many generation requests from the user. Limit = [%s] requests per [%s] seconds",
                limitForPeriod, refreshPeriodSeconds));
        this.retryAfterSeconds = refreshPeriodSeconds;
    }
}
