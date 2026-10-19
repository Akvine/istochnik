package ru.akvine.istochnik.controllers.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ConfigDto {
    private boolean unique;

    private boolean notNull;

    private boolean valid;

    private String rangeType;

    private String start;

    private String end;

    private String step;

    private DistributionDto distribution;

    private Integer length;

    private Object constant;

    private Set<Set<String>> dictionaries;

    /**
     * Веса значений словарей стратегии dictionary: значение - вес. Значения без веса получают вес 1
     */
    private Map<String, Double> weights;

    private Set<String> regexps;

    private String expression;

    private List<String> topics = List.of();

    private String language;

    private String seed;
}
