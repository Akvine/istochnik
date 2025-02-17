package ru.akvine.istochnik.services.generators.number.integer.configs;

import jakarta.annotation.Nullable;

public class IntegerConstantsConfig {
    @Nullable
    private Integer value;
    private int size;

    @Nullable
    public Integer getValue() {
        return value;
    }

    public void setValue(@Nullable Integer value) {
        this.value = value;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
