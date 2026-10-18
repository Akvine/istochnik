package ru.akvine.istochnik.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@UtilityClass
@Slf4j
public class ThreadsUtils {
    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder()
                .setDaemon(false)
                .setNameFormat(name +"-%d")
                .setUncaughtExceptionHandler((t, e) -> log.error("Uncaught exception in thread [{}]", t.getName(), e))
                .build();
    }
}
