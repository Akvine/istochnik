package ru.akvine.istochnik.services.file;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.exceptions.FileTableGenerationException;
import ru.akvine.istochnik.services.dto.Column;
import ru.akvine.istochnik.services.dto.Table;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvFileTableGenerator implements FileTableGenerator {
    @Override
    public byte[] generate(Table table) {
        CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(";").build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            int rowsCount = table.getRowsCount();
            for (int rowIndex = 0; rowIndex < rowsCount; ++rowIndex) {
                List<Object> values = new ArrayList<>();

                for (Column column : table.getColumns()) {
                    values.add(column.getValues().get(rowIndex));
                }

                csvPrinter.printRecord(values);
            }


            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException exception) {
            String errorMessage = String.format(
                    "Fail convert records to %s file type. Message = [%s]",
                    getType(),
                    exception.getMessage()
            );
            throw new FileTableGenerationException(errorMessage);
        }
    }

    @Override
    public FileType getType() {
        return FileType.CSV;
    }
}
