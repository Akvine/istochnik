package ru.akvine.istochnik.services;

import java.util.List;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.services.dto.GenerateColumn;

public interface GenerationHandler {
    List<?> handle(GenerateColumn generateColumn);

    GenerationStrategy getStrategy();
}
