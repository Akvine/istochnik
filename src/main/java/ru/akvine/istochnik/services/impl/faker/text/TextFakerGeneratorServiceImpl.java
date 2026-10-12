package ru.akvine.istochnik.services.impl.faker.text;

import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;
import ru.akvine.istochnik.services.dto.Config;

/**
 * Текст-заполнитель (lorem ipsum) ровно заданной длины. Язык колонки на него не влияет
 */
@Service
public class TextFakerGeneratorServiceImpl implements FakerGeneratorService {
    public static final int DEFAULT_LENGTH = 100;

    private static final char SENTENCES_SEPARATOR = ' ';

    @Override
    public String generate(Faker faker) {
        return generate(faker, DEFAULT_LENGTH);
    }

    @Override
    public String generate(Faker faker, Config config) {
        Integer length = config.getLength();
        return generate(faker, length == null ? DEFAULT_LENGTH : length);
    }

    @Override
    public Topic getByTopic() {
        return Topic.TEXT;
    }

    private String generate(Faker faker, int length) {
        StringBuilder text = new StringBuilder(length);
        while (text.length() < length) {
            if (!text.isEmpty()) {
                text.append(SENTENCES_SEPARATOR);
            }
            text.append(faker.lorem().sentence());
        }
        text.setLength(length);

        // Текст обрезается до нужной длины посреди предложения. Пробел в конце заменяется буквой:
        // хранилища и форматы часто отбрасывают концевые пробелы, и длина значения стала бы меньше заданной
        int last = length - 1;
        if (last >= 0 && text.charAt(last) == SENTENCES_SEPARATOR) {
            text.setCharAt(last, faker.lorem().character(false));
        }
        return text.toString();
    }
}
