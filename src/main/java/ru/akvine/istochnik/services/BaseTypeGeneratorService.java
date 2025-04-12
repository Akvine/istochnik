package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.BaseType;

public interface BaseTypeGeneratorService extends GeneratorService {
    BaseType getType();
}
