package name.remal.gradle_plugins.load_content.internal;

import static name.remal.gradle_plugins.load_content.internal.SharedServices.getBuildService;

import java.net.URI;
import java.util.concurrent.Callable;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.impl.cache.CacheKeyGenerator;
import org.apache.hc.core5.http.HttpHost;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.provider.Property;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public interface HostRateLimiter extends ContentLoaderBuildService<HostRateLimiter.Parameters> {

    static HostRateLimiter getHostRateLimiterFor(Gradle gradle) {
        return getBuildService(gradle, HostRateLimiter.class, HostRateLimiterImpl.class);
    }


    abstract class Parameters implements BuildServiceParameters {

        public abstract Property<Integer> getMaxParallelRequestPerHost();

        {
            getMaxParallelRequestPerHost().convention(4);
        }

    }


    @Nullable
    <T> T withPermit(String host, Callable<@Nullable T> action);

    @Nullable
    @SneakyThrows
    default <T> T withPermit(URI uri, Callable<@Nullable T> action) {
        uri = CacheKeyGenerator.normalize(uri);
        var hostName = HttpHost.create(uri).getHostName();
        return withPermit(hostName, action);
    }

}
