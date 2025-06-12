package ru.akvine.istochnik.services.converters.string.hash;

import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;
import ru.akvine.istochnik.services.converters.string.StringConverter;
import ru.akvine.istochnik.utils.CryptoUtils;

import java.util.List;

@Service
public class SHA256Converter extends StringConverter<String, String> {
    @Override
    public List<String> convert(List<String> input, String[] arguments) {
        return input.stream().map(CryptoUtils::hashSHA256).toList();
    }

    @Override
    public ConverterType getName() {
        return ConverterType.SHA256;
    }
}
