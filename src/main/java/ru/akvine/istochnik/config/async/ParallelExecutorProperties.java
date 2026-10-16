package ru.akvine.istochnik.config.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "parallel.executor")
public class ParallelExecutorProperties {
    /**
     * Размер пула потоков генерации. Значение 0 или меньше - по числу доступных ядер процессора
     */
    private int threadsCount;

    private int queueCapacity;
}
