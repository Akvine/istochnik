package ru.akvine.istochnik.services.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.akvine.istochnik.enums.RangeType;
import ru.akvine.istochnik.enums.Topic;

import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;

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

    private List<Converter> converters;

    private Integer length;

    private Object constant;

    private List<List<String>> dictionaries;

    private Set<String> regexps;

    /**
     * Используется только для GenerationType = Faker
     */
    private List<Topic> topics;

    private String lang;

    private RandomGenerator randomGenerator;
}
