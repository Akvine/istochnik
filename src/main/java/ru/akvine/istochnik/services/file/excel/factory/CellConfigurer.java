package ru.akvine.istochnik.services.file.excel.factory;

import org.apache.poi.ss.usermodel.Cell;

public interface CellConfigurer {
    void configure(Cell cell, Object object);

    Class<?> getType();
}
