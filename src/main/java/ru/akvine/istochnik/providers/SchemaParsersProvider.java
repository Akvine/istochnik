package ru.akvine.istochnik.providers;

import java.util.Map;
import ru.akvine.istochnik.enums.SchemaType;
import ru.akvine.istochnik.services.schema.SchemaParser;

public record SchemaParsersProvider(Map<SchemaType, SchemaParser> parsers) {
    public SchemaParser getByType(SchemaType type) {
        if (parsers.containsKey(type)) {
            return parsers.get(type);
        }

        throw new UnsupportedOperationException("Schema parser not supported with type = [" + type + "] by app!");
    }
}
