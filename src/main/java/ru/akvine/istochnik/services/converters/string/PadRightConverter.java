package ru.akvine.istochnik.services.converters.string;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.ConverterType;

/**
 * Дополняет строку справа: AB превращается в AB______
 */
@Service
public class PadRightConverter extends AbstractPadConverter {
    @Override
    protected String pad(String value, int length, char padChar) {
        return StringUtils.rightPad(value, length, padChar);
    }

    @Override
    public ConverterType getName() {
        return ConverterType.PAD_RIGHT;
    }
}
