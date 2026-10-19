package ru.akvine.istochnik.services.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.random.RandomGenerator;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.distribution.Distribution;
import ru.akvine.istochnik.services.distribution.UniformDistribution;

@Data
@Accessors(chain = true)
public class Config {
    private int size;

    private boolean unique;

    private boolean notNull;

    private boolean valid;

    private RangeType rangeType;

    private String start;

    private String end;

    private String step;

    /**
     * Распределение случайных значений внутри диапазона от start до end
     */
    private Distribution distribution = UniformDistribution.INSTANCE;

    private List<Converter> converters;

    private Integer length;

    private Object constant;

    private List<List<String>> dictionaries;

    /**
     * Веса значений словарей. Если они не заданы, значения равновероятны
     */
    private Map<String, Double> weights;

    private Set<String> regexps;

    /**
     * Используется только для GenerationType = Expression
     */
    private String expression;

    /**
     * Используется только для GenerationType = Faker
     */
    private List<Topic> topics;

    private String lang;

    private RandomGenerator randomGenerator;

    private String seed;
}
