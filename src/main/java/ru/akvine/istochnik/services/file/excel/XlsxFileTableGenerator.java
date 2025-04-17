package ru.akvine.istochnik.services.file.excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.exceptions.ReportGenerationException;
import ru.akvine.istochnik.managers.CellConfigurersManager;
import ru.akvine.istochnik.services.dto.Column;
import ru.akvine.istochnik.services.dto.Table;
import ru.akvine.istochnik.services.file.FileTableGenerator;
import ru.akvine.istochnik.utils.Asserts;
import ru.akvine.istochnik.utils.POIUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class XlsxFileTableGenerator implements FileTableGenerator {
    private final static String SHEET_NAME = "sheet";

    private final CellConfigurersManager cellConfigurersManager;

    @Override
    public byte[] generate(Table table) {
        Asserts.isNotNull(table);

        List<Column> columns = table.getColumns();
        int rowsCount = table.getRowsCount();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            createHeaders(sheet, columns);

            for (int rowNumber = 0; rowNumber < rowsCount; ++rowNumber) {
                Row row = sheet.createRow(rowNumber + 1);

                for (int columnIndex = 0; columnIndex < columns.size(); ++columnIndex) {
                    Cell createdCell = row.createCell(columnIndex);
                    Object value = columns.get(columnIndex).getValues().get(rowNumber);

                    if (value == null) {
                        createdCell.setCellValue("");
                        createdCell.setCellValue(createdCell.getStringCellValue());
                    } else {
                        cellConfigurersManager
                                .getByClass(value.getClass())
                                .configure(createdCell, value);
                    }

                }
            }

            return POIUtils.mapToBytes(workbook);
        } catch (IOException exception) {
            String errorMessage = String.format("Error while generate report: [%s]", exception.getMessage());
            throw new ReportGenerationException(errorMessage);
        }
    }

    @Override
    public FileType getType() {
        return FileType.XLSX;
    }

    private void createHeaders(Sheet sheet, List<Column> columns) {
        Row headersRow = sheet.createRow(0);

        for (int cellNumber = 0; cellNumber < columns.size(); ++cellNumber) {
            Cell cell = headersRow.createCell(cellNumber);
            cell.setCellValue(columns.get(cellNumber).getName());
        }
    }
}
