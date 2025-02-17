package ru.akvine.istochnik.services.generators.date.localdatetime.configs;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;


public class LocalDateTimeConstantsConfig {
    @Nullable
    public LocalDateTime getConstant() {
        return constant;
    }

    public LocalDateTimeConstantsConfig setConstant(@Nullable LocalDateTime constant) {
        this.constant = constant;
        return this;
    }

    public int getSize() {
        return size;
    }

    public LocalDateTimeConstantsConfig setSize(int size) {
        this.size = size;
        return this;
    }

    @Nullable
    private LocalDateTime constant;
    private int size;
}
