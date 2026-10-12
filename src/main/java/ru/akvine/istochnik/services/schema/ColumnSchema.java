package ru.akvine.istochnik.services.schema;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Колонка таблицы, прочитанная из схемы
 */
@Data
@Accessors(chain = true)
public class ColumnSchema {
    private String name;
    private ColumnType type;
    private boolean notNull;

    /**
     * Значения колонки не повторяются: первичный ключ, ограничение UNIQUE или уникальный индекс
     */
    private boolean unique;

    /**
     * Значения по умолчанию берутся из последовательности: serial, identity, AUTO_INCREMENT
     */
    private boolean autoIncrement;

    /**
     * Значение всегда задаёт сама база данных, вставить своё нельзя: GENERATED ALWAYS, вычисляемая колонка
     */
    private boolean databaseGenerated;

    /**
     * Таблица и колонка, на которые ссылается внешний ключ, например roles(id)
     */
    private String reference;

    /**
     * Допустимые значения колонки, если её тип - перечисление
     */
    private List<String> allowedValues = List.of();
}
