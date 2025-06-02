package ru.akvine.istochnik.config.async;

import java.util.concurrent.ThreadPoolExecutor;

public record TaskExecutor(ThreadPoolExecutor executor) {
}
