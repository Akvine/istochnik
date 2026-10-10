package ru.akvine.istochnik.validators.custom;

import java.util.List;
import ru.akvine.compozit.commons.istochnik.ColumnDto;
import ru.akvine.istochnik.enums.CustomType;

/**
 * Проверка параметров, специфичных для пользовательского типа.
 * Выполняется в дополнение к проверке его базового типа
 */
public interface CustomTypeValidator {
    List<String> validate(ColumnDto column, int rowsCount);

    CustomType getCustomType();
}
