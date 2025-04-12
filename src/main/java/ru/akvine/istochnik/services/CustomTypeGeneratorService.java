package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.CustomType;

public interface CustomTypeGeneratorService extends GeneratorService {
    CustomType getType();
}
