package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class SortDescStringConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input,
                                String[] arguments,
                                RandomGenerator randomGenerator,
                                double probability) {
        List<String> result = new ArrayList<>(input);
        result.sort(Comparator.nullsFirst(Comparator.reverseOrder()));
        return result;
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SORT_DESC;
    }
}
