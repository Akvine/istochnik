package ru.akvine.istochnik.services.generators;

public class Config {
    public int getSize() {
        return size;
    }

    protected final int size;

    public Config(int size) {
        this.size = size;
    }
}
