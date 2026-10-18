package ru.akvine.istochnik.controllers.dto.strategies;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.controllers.dto.SuccessfulResponse;

@Data
@Accessors(chain = true)
public class StrategiesListResponse extends SuccessfulResponse {
    private List<StrategyDto> strategies;
    private int count;
}
