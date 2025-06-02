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
    private int threadsCount;
    private int queueCapacity;
}
