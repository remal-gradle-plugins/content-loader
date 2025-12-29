package name.remal.gradle_plugins.load_content;

import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import org.gradle.api.Buildable;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.file.FileSystemLocationProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.PathSensitive;
import org.jspecify.annotations.Nullable;

@CacheableTask
public abstract class LoadContent extends AbstractLoadContent {

    private final Property<URI> sourceUri = getObjects().property(URI.class);

    /**
     * This method sets {@link #getSourceUri()}. {@code source} can be of types:
     * <ul>
     *     <li>{@link URI}
     *     <li>{@link URL}
     *     <li>{@link File}
     *     <li>{@link Path}
     *     <li>{@link CharSequence}
     *     <li>{@link FileSystemLocation} (must represent a regular file, for example {@link RegularFile})
     *     <li>{@link Provider} of any of the types above
     * </ul>
     *
     * <p>If the resulting URI is relative (i.e. not {@link URI#isAbsolute()}),
     * it will be converted to an absolute {@code file:} URI by resolving against the project directory.
     */
    public void from(@Nullable Object source) {
        sourceUri.set(toUriProvider(source));
        if (source instanceof Buildable || source instanceof FileSystemLocationProperty<?>) {
            sourceUriDependency = source;
        }
    }

    @Input
    public Provider<URI> getSourceUri() {
        return sourceUri;
    }

    @Nullable
    private volatile Object sourceUriDependency;

    {
        dependsOn(getProviders().provider(() -> sourceUriDependency));
    }

    @InputFile
    @PathSensitive(RELATIVE)
    @org.gradle.api.tasks.Optional
    protected abstract RegularFileProperty getSourceFile();

    {
        getSourceFile().fileProvider(getProviders().provider(() -> {
            var sourceUri = getSourceUri().getOrNull();
            if (sourceUri == null || !"file".equals(sourceUri.getScheme())) {
                return null;
            }

            return new File(sourceUri);
        }));
    }

}
