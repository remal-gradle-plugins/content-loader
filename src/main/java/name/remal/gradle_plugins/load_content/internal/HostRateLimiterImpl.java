package name.remal.gradle_plugins.load_content.internal;

import static lombok.AccessLevel.PUBLIC;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
@NoArgsConstructor(access = PUBLIC, onConstructor_ = {@Inject})
public abstract class HostRateLimiterImpl implements HostRateLimiter {

    private final transient ConcurrentMap<String, Semaphore> semaphores = new ConcurrentHashMap<>();

    @Override
    @Nullable
    @SneakyThrows
    public <T> T withPermit(String host, Callable<@Nullable T> action) {
        var semaphore = semaphores.computeIfAbsent(host, __ -> {
            int maxParallelRequestPerHost = getParameters().getMaxParallelRequestPerHost().get();
            if (maxParallelRequestPerHost <= 0) {
                throw new IllegalArgumentException("Max parallel requests per host must be greater than 0");
            }
            return new Semaphore(maxParallelRequestPerHost);
        });
        semaphore.acquire();
        try {
            return action.call();
        } finally {
            semaphore.release();
        }
    }

}
