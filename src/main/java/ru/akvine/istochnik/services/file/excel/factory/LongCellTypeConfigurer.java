package ru.akvine.istochnik.services.file.excel.factory;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;

@Service
public class LongCellTypeConfigurer implements CellConfigurer {
    @Override
    public void configure(Cell cell, Object object) {
        cell.setCellValue((Long) object);
    }

    @Override
    public Class<?> getType() {
        return Long.class;
    }
}
