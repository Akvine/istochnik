package ru.akvine.istochnik.controllers.validators;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.akvine.istochnik.config.schema.SchemaConversionProperties;
import ru.akvine.istochnik.enums.FileType;
import ru.akvine.istochnik.enums.SchemaType;
import ru.akvine.istochnik.exceptions.SchemaConversionDisabledException;
import ru.akvine.istochnik.exceptions.SchemaConversionException;
import ru.akvine.istochnik.services.SchemaConversionService;
import ru.akvine.istochnik.services.limits.GenerationLimitsService;

@Component
@RequiredArgsConstructor
public class SchemaValidator {
    private static final int BYTES_IN_KILOBYTE = 1024;

    private final SchemaConversionService schemaConversionService;
    private final SchemaConversionProperties schemaConversionProperties;
    private final GenerationLimitsService generationLimitsService;

    public void verifyConvertSchemaRequest(MultipartFile file, String type, int size, String fileType) {
        if (!schemaConversionService.isEnabled()) {
            throw new SchemaConversionDisabledException();
        }

        if (file == null || file.isEmpty()) {
            throw new SchemaConversionException("Schema file can't be empty");
        }

        long maxFileSize = (long) schemaConversionProperties.getMaxFileSizeKb() * BYTES_IN_KILOBYTE;
        if (file.getSize() > maxFileSize) {
            throw new SchemaConversionException(String.format(
                    "Schema file size = [%s] bytes is greater than max = [%s] bytes", file.getSize(), maxFileSize));
        }

        int maxGenerationRowsSize = generationLimitsService.getLimits().maxRowsSize();
        if (size < 0) {
            throw new SchemaConversionException("size can't be less than 0");
        }
        if (size > maxGenerationRowsSize) {
            throw new SchemaConversionException(
                    String.format("Can't generate rows size = [%s] more than max = [%s]", size, maxGenerationRowsSize));
        }

        try {
            FileType.from(fileType);
            SchemaType.resolve(type, file.getOriginalFilename());
        } catch (RuntimeException exception) {
            throw new SchemaConversionException(exception.getMessage());
        }
    }
}
