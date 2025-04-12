package ru.akvine.istochnik.services;

import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.services.dto.GenerateColumn;

import java.util.List;

public interface GenerationHandler {
    List<?> handle(GenerateColumn generateColumn);

    GenerationStrategy getStrategy();
}
