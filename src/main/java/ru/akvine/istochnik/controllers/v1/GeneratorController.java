package ru.akvine.istochnik.controllers.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.akvine.compozit.commons.istochnik.GenerateTableRequest;
import ru.akvine.istochnik.controllers.converters.GeneratorConverter;
import ru.akvine.istochnik.controllers.v1.meta.GeneratorControllerMeta;
import ru.akvine.istochnik.controllers.validators.GeneratorValidator;
import ru.akvine.istochnik.services.FileTableService;
import ru.akvine.istochnik.services.GeneratorFacade;
import ru.akvine.istochnik.services.dto.GenerateData;
import ru.akvine.istochnik.services.dto.Table;

@RestController
@RequiredArgsConstructor
public class GeneratorController implements GeneratorControllerMeta {
    private final GeneratorValidator generatorValidator;
    private final GeneratorConverter generatorConverter;
    private final GeneratorFacade generatorFacade;
    private final FileTableService fileTableService;

    @Override
    public ResponseEntity<?> generate(@RequestParam(value = "tableName", required = false, defaultValue = "response") String tableName,
                                      @RequestBody @Valid GenerateTableRequest request) {
        generatorValidator.verifyGenerateTableRequest(request);
        GenerateData generateData = generatorConverter.convertToGenerateData(request);
        Table table = generatorFacade.generate(generateData);
        byte[] file = fileTableService.generateFile(generateData.getFileType(), table);
        return generatorConverter.convertToResponse(file, generateData.getFileType(), tableName);
    }
}
