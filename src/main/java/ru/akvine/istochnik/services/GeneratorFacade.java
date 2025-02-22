package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.GenerateData;

import java.util.List;

public interface GeneratorFacade {
    List<?> generate(GenerateData generateData);
}
