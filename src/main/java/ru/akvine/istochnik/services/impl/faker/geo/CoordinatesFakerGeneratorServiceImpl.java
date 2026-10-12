package ru.akvine.istochnik.services.impl.faker.geo;

import java.util.Locale;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import ru.akvine.istochnik.enums.Topic;
import ru.akvine.istochnik.services.FakerGeneratorService;

/**
 * Случайные географические координаты - широта и долгота одной точки в градусах: 55.751244, 37.618423.
 * Точки распределены равномерно по широте и долготе, поэтому большая их часть приходится на океаны
 */
@Service
public class CoordinatesFakerGeneratorServiceImpl implements FakerGeneratorService {
    private static final double MAX_LATITUDE = 90;
    private static final double MAX_LONGITUDE = 180;

    // Шесть знаков после запятой - точность около 10 сантиметров
    private static final String FORMAT = "%.6f, %.6f";

    @Override
    public String generate(Faker faker) {
        double latitude = faker.random().nextDouble(-MAX_LATITUDE, MAX_LATITUDE);
        double longitude = faker.random().nextDouble(-MAX_LONGITUDE, MAX_LONGITUDE);

        // Разделитель дробной части - всегда точка, каким бы ни был язык колонки: значения должны разбираться как числа
        return String.format(Locale.ROOT, FORMAT, latitude, longitude);
    }

    @Override
    public Topic getByTopic() {
        return Topic.COORDINATES;
    }
}
