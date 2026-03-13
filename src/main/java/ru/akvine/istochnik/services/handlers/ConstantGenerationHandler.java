package ru.akvine.istochnik.services.handlers;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.services.GenerationHandler;
import ru.akvine.istochnik.services.dto.GenerateColumn;
import ru.akvine.istochnik.services.generators.ConstantGenerator;

@Service
public class ConstantGenerationHandler implements GenerationHandler {
    @Override
    public List<?> handle(GenerateColumn generateColumn) {
        return new ConstantGenerator<>()
                .generate(
                        generateColumn.getConfig().getSize(),
                        generateColumn.getConfig().getConstant());
    }

    @Override
    public GenerationStrategy getStrategy() {
        return GenerationStrategy.CONSTANT;
    }
}
