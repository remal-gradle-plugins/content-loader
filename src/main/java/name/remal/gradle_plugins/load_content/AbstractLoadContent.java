package name.remal.gradle_plugins.load_content;

import static java.lang.String.format;
import static name.remal.gradle_plugins.load_content.internal.Constants.PLUGIN_ID;
import static name.remal.gradle_plugins.toolkit.LayoutUtils.getRootDirOf;
import static name.remal.gradle_plugins.toolkit.ObjectUtils.unwrapProviders;
import static name.remal.gradle_plugins.toolkit.PathUtils.normalizePath;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.jspecify.annotations.Nullable;

@CacheableTask
abstract class AbstractLoadContent extends DefaultTask {

    @Input
    @org.gradle.api.tasks.Optional
    public abstract Property<ContentProcessor> getContentProcessor();


    @Internal
    public abstract DirectoryProperty getOutputDir();

    {
        getOutputDir().convention(getLayout().getBuildDirectory().dir(format(
            "%s/%s",
            PLUGIN_ID,
            getName()
        )));
    }


    @Internal
    public abstract DirectoryProperty getRootDir();

    {
        getRootDir().fileValue(getRootDirOf(getProject()));
    }


    /**
     * {@code source} can be of types:
     * <ul>
     *     <li>{@link URI}
     *     <li>{@link URL}
     *     <li>{@link File}
     *     <li>{@link Path}
     *     <li>{@link CharSequence}
     *     <li>{@link FileSystemLocation}
     *     <li>{@link Provider} of any of the types above
     * </ul>
     *
     * <p>If the resulting URI is relative (i.e. not {@link URI#isAbsolute()}),
     * it will be converted to an absolute {@code file:} URI by resolving against the project directory.
     */
    @Nullable
    @SneakyThrows
    protected URI toUri(@Nullable Object object) {
        var projectDirPath = getLayout().getProjectDirectory().getAsFile().toPath();

        object = unwrapProviders(object);
        URI result = null;
        while (object != null) {
            if (object instanceof URI it) {
                result = it;
                break;
            }

            if (object instanceof String it) {
                try {
                    var path = Paths.get(it);
                    if (!path.isAbsolute()) {
                        path = normalizePath(projectDirPath.resolve(path));
                    }
                    result = path.toUri();
                    break;
                } catch (InvalidPathException ignored) {
                    // do nothing
                }
            }

            object = switch (object) {
                case URL it -> it.toURI();
                case String it -> URI.create(it);
                case File it -> it.getPath();
                case Path it -> it.toString();
                case CharSequence it -> it.toString();
                case FileSystemLocation it -> it.getAsFile();
                default -> throw new UnsupportedOperationException("Cannot convert to URI: " + object);
            };
        }

        if (result != null && !result.isAbsolute()) {
            var resultPath = Path.of(result.getPath());
            resultPath = projectDirPath.resolve(resultPath).normalize();
            result = resultPath.toUri();
        }

        return result;
    }

    protected Provider<URI> toUriProvider(@Nullable Object object) {
        return getProviders().provider(() -> toUri(object));
    }


    @Inject
    protected abstract ProviderFactory getProviders();

    @Inject
    protected abstract ProjectLayout getLayout();

    @Inject
    protected abstract ObjectFactory getObjects();

}
