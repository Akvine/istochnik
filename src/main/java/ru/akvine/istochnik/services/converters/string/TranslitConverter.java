package ru.akvine.istochnik.services.converters.string;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Alphabets;
import ru.akvine.istochnik.enums.ConverterType;

import java.util.List;

@Service
public class TranslitConverter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(value -> {
            StringBuilder sb = new StringBuilder();
            char[] chars = value.toCharArray();
            for (char charValue : chars) {
                if (Character.isAlphabetic(charValue) && Alphabets.RUSSIAN.getChars().contains(charValue)) {
                    sb.append(Alphabets.TRANSLIT_MAP.get(charValue));
                }
            }

            return sb.toString();
        }).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.TRANSLIT;
    }
}
