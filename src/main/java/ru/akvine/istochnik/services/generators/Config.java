package ru.akvine.istochnik.services.generators;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.random.RandomGenerator;

@Getter
@AllArgsConstructor
public class Config {
    protected final int size;
    protected RandomGenerator randomGenerator;

    public Config() {
        this.size = 0;
        this.randomGenerator = null;
    }
}
