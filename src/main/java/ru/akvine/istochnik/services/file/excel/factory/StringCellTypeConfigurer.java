package ru.akvine.istochnik.services.file.excel.factory;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;

@Service
public class StringCellTypeConfigurer implements CellConfigurer {
    @Override
    public void configure(Cell cell, Object object) {
        cell.setCellValue((String) object);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
