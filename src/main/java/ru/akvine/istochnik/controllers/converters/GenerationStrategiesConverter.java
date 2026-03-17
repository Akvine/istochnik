package ru.akvine.istochnik.controllers.converters;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.akvine.istochnik.controllers.dto.strategies.StrategiesListResponse;
import ru.akvine.istochnik.controllers.dto.strategies.StrategyDto;
import ru.akvine.istochnik.enums.GenerationStrategy;
import ru.akvine.istochnik.enums.Language;
import ru.akvine.istochnik.services.MessageResolverService;

@Component
@RequiredArgsConstructor
public class GenerationStrategiesConverter {
    private final MessageResolverService messageResolverService;

    public StrategiesListResponse convertToStrategiesListResponse(Language language) {
        List<StrategyDto> strategies = Arrays.stream(GenerationStrategy.values())
                .map(value -> buildStrategyDto(value, language))
                .toList();
        return new StrategiesListResponse().setStrategies(strategies).setCount(strategies.size());
    }

    private StrategyDto buildStrategyDto(GenerationStrategy strategy, Language language) {
        return new StrategyDto()
                .setName(strategy.getName())
                .setDescription(messageResolverService.message(strategy.getCode(), language))
                .setSupportsNotNull(strategy.isSupportsNotNull())
                .setSupportsUnique(strategy.isSupportsUnique())
                .setSupportsValid(strategy.isSupportsValid());
    }
}
