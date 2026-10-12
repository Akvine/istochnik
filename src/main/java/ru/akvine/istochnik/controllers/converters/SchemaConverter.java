package ru.akvine.istochnik.controllers.converters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.akvine.compozit.commons.utils.Asserts;
import ru.akvine.istochnik.controllers.dto.schema.ConvertSchemaResponse;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.SchemaType;
import ru.akvine.istochnik.exceptions.SchemaConversionException;
import ru.akvine.istochnik.services.dto.schema.ConvertSchema;
import ru.akvine.istochnik.services.dto.schema.ConvertedSchema;

@Component
public class SchemaConverter {
    // Метка порядка байтов, которую редакторы добавляют в начало файла в кодировке UTF-8
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    public ConvertSchema convertToConvertSchema(MultipartFile file, String type, int size, String fileType) {
        Asserts.isNotNull(file);

        return new ConvertSchema()
                .setType(SchemaType.resolve(type, file.getOriginalFilename()))
                .setContent(readContent(file))
                .setSize(size)
                .setFileType(FileType.from(fileType));
    }

    public ConvertSchemaResponse convertToConvertSchemaResponse(ConvertedSchema convertedSchema) {
        Asserts.isNotNull(convertedSchema);

        return new ConvertSchemaResponse()
                .setRequests(convertedSchema.getRequests())
                .setCount(convertedSchema.getRequests().size())
                .setWarnings(convertedSchema.getWarnings());
    }

    private String readContent(MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            return StringUtils.removeStart(content, BYTE_ORDER_MARK);
        } catch (IOException exception) {
            throw new SchemaConversionException("Can't read schema file: " + exception.getMessage(), exception);
        }
    }
}
