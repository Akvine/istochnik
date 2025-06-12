package ru.akvine.istochnik.services;

import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.Converter;

import java.util.List;

public interface GeneratorService {
    List<?> generate(Config config, List<Converter> converters);
}
