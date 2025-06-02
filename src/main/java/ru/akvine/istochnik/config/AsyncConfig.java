package ru.akvine.istochnik.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.akvine.compozit.commons.utils.ThreadsUtils;
import ru.akvine.istochnik.config.async.ParallelExecutorProperties;
import ru.akvine.istochnik.config.async.TaskExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final ParallelExecutorProperties parallelExecutorProperties;

    private static final String PARALLEL_EXECUTOR_BASE_NAME = "parallel-generation-data-executor";

    @Bean
    public TaskExecutor parallelGenerationExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                parallelExecutorProperties.getThreadsCount(),
                parallelExecutorProperties.getThreadsCount(),
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(parallelExecutorProperties.getQueueCapacity()),
                ThreadsUtils.newThreadFactory(PARALLEL_EXECUTOR_BASE_NAME));
        return new TaskExecutor(executor);
    }
}
