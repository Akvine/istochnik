package ru.akvine.istochnik.services.file.excel.factory;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;

@Service
public class IntegerCellTypeConfigurer implements CellConfigurer {
    @Override
    public void configure(Cell cell, Object object) {
        cell.setCellValue((Integer) object);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
