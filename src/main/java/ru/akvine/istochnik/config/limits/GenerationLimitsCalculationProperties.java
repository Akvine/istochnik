package ru.akvine.istochnik.config.limits;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix = "max.generation.calculation")
public class GenerationLimitsCalculationProperties {
    /**
     * Рассчитывать лимиты строк, колонок и ячеек по ресурсам машины при запуске, а не брать их из настроек
     */
    private boolean enabled = false;

    /**
     * Доля кучи, которую можно отдать под генерацию. Остальное остаётся самому приложению
     */
    @Positive
    @DecimalMax("1.0")
    private double memoryShare = 0.7;

    /**
     * Сколько байт кучи в пике занимает одна ячейка таблицы: значение, его строка в файле и копии файла
     */
    @Positive
    private int bytesPerCell = 300;

    /**
     * То же для файлов XLSX: книга Excel строится в памяти целиком, и ячейка в ней обходится намного дороже
     */
    @Positive
    private int xlsxBytesPerCell = 3000;

    /**
     * Сколько пользователей одновременно могут отправить запрос предельного размера: память и пул потоков делятся
     * между ними поровну
     */
    @Positive
    private int users = 1;
}
