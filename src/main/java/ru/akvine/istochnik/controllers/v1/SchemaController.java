package ru.akvine.istochnik.controllers.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.akvine.compozit.commons.dto.Response;
import ru.akvine.istochnik.controllers.converters.SchemaConverter;
import ru.akvine.istochnik.controllers.v1.meta.SchemaControllerMeta;
import ru.akvine.istochnik.controllers.validators.SchemaValidator;
import ru.akvine.istochnik.services.SchemaConversionService;
import ru.akvine.istochnik.services.dto.schema.ConvertSchema;
import ru.akvine.istochnik.services.dto.schema.ConvertedSchema;

@RestController
@RequiredArgsConstructor
public class SchemaController implements SchemaControllerMeta {
    private final SchemaValidator schemaValidator;
    private final SchemaConverter schemaConverter;
    private final SchemaConversionService schemaConversionService;

    @Override
    public Response convert(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "size", required = false, defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(value = "fileType", required = false, defaultValue = DEFAULT_FILE_TYPE) String fileType) {
        schemaValidator.verifyConvertSchemaRequest(file, type, size, fileType);
        ConvertSchema convertSchema = schemaConverter.convertToConvertSchema(file, type, size, fileType);
        ConvertedSchema convertedSchema = schemaConversionService.convert(convertSchema);
        return schemaConverter.convertToConvertSchemaResponse(convertedSchema);
    }
}
