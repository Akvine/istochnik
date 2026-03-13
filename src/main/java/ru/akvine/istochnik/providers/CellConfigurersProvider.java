package ru.akvine.istochnik.providers;

import java.util.Map;
import ru.akvine.istochnik.exceptions.UnsupportedTypeGenerationException;
import ru.akvine.istochnik.services.file.excel.factory.CellConfigurer;

public record CellConfigurersProvider(Map<Class<?>, CellConfigurer> converts) {
    public CellConfigurer getByClass(Class<?> clazz) {
        if (converts.containsKey(clazz)) {
            return converts.get(clazz);
        }

        throw new UnsupportedTypeGenerationException(
                "Excel cell converter for class = [" + clazz.getSimpleName() + "] is not supported!");
    }
}
