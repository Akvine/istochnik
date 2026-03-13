package ru.akvine.istochnik.services;

import java.util.List;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;

public interface GeneratorService {
    List<?> generate(Config config, List<Converter> converters);
}
