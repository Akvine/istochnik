package ru.akvine.istochnik.controllers.dto.strategies;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StrategyDto {
    private String name;
    private String description;

    private boolean supportsUnique;
    private boolean supportsNotNull;
    private boolean supportsValid;
}
