package ru.akvine.istochnik.services.handlers;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.generators.ConstantGenerator;

import java.util.List;


@Service
public class ConstantGenerationHandler implements GenerationHandler {
    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        return new ConstantGenerator<>().generate(
                generateColumn.getConfig().getSize(),
                generateColumn.getConfig().getConstant());
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.CONSTANT;
    }
}
