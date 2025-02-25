package ru.akvine.istochnik.controllers.converters;

import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.ColumnDto;
import ru.akvine.istochnik.controllers.dto.ConfigDto;
import ru.akvine.istochnik.controllers.dto.GenerateTableRequest;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.enums.Type;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.dto.Config;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.utils.Asserts;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeneratorConverter {
    public GenerateData convertToGenerateData(GenerateTableRequest request) {
        Asserts.isNotNull(request);

        int size = request.getSize();
        List<GenerateColumn> generateColumns = new ArrayList<>();
        for (ColumnDto column : request.getColumns()) {
            generateColumns.add(new GenerateColumn()
                    .setName(column.getName())
                    .setType(Type.from(column.getType()))
                    .setConfig(buildConfig(size, column.getConfig())));
        }

        return new GenerateData()
                .setSize(size)
                .setGenerateColumns(generateColumns);
    }

    public ResponseEntity<?> convertToResponse(byte[] file, String reportType) {
        Asserts.isNotNull(file);
        Asserts.isNotNull(reportType);

        String mimeType;
        switch (reportType.toLowerCase()) {
            case "csv": {
                mimeType = "application/csv";
                break;
            }
            case "xlsx": {
                mimeType = "application/xlsx";
                break;
            }
            default: throw new UnsupportedOperationException("Mime type = [" + reportType + "] is not supported!");
        }

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(file);
    }

    private Config buildConfig(int size, ConfigDto configDto) {
        return new Config()
                .setSize(size)
                .setNotNull(configDto.getNotNull())
                .setUnique(configDto.getUnique())
                .setRangeType(RangeType.valueOf(configDto.getRangeType()))
                .setStart(configDto.getStart())
                .setEnd(configDto.getEnd())
                .setStep(configDto.getStep());
    }
}
