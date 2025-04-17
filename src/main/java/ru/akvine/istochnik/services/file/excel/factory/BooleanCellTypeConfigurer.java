package ru.akvine.istochnik.services.file.excel.factory;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;

@Service
public class BooleanCellTypeConfigurer implements CellConfigurer {
    @Override
    public void configure(Cell cell, Object object) {
        cell.setCellValue(Boolean.parseBoolean((String) object));
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
