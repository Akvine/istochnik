package ru.akvine.istochnik.services.generators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Config {
    protected final int size;

    public Config() {
        this.size = 0;
    }
}
