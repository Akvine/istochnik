package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

public interface GeneratorFacade {
    Table generate(GenerateData generateData);
}
