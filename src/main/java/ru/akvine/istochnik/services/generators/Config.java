package ru.akvine.istochnik.services.generators;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.random.RandomGenerator;

@Getter
@AllArgsConstructor
public class Config {
    protected final int size;
    protected RandomGenerator randomGenerator;
    protected String seed;

    public Config(int size, RandomGenerator randomGenerator) {
        this.size = size;
        this.randomGenerator = randomGenerator;
    }

    public Config() {
        this.size = 0;
    }
}
