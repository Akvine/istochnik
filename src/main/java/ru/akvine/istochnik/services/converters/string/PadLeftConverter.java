package ru.akvine.istochnik.services.converters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

/**
 * Дополняет строку слева: 42 превращается в 00000042
 */
@Service
public class PadLeftConverter extends AbstractPadConverter {
    @Override
    protected String pad(String value, int length, char padChar) {
        return StringUtils.leftPad(value, length, padChar);
    }

    @Override
    public ConverterType getName() {
        return ConverterType.PAD_LEFT;
    }
}
