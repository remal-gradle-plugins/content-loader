package name.remal.gradle_plugins.load_content;

import static name.remal.gradle_plugins.toolkit.ObjectUtils.unwrapProviders;
import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize;

import java.io.File;
import java.net.URI;
import java.net.URL;
import javax.inject.Inject;
import name.remal.gradle_plugins.load_content.loader.file.LoadFileContent;
import org.gradle.api.Buildable;
import org.gradle.api.Named;
import org.gradle.api.file.FileSystemLocationProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;

public abstract class LoadContentFactory implements Named {

    private final String name;

    protected LoadContentFactory(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }


    /**
     * {@code file} can be of types:
     * <ul>
     *     <li>{@link RegularFile}
     *     <li>{@link File} (resolved against project directory if relative)
     *     <li>{@link String} (resolved against project directory if relative)
     *     <li>{@link URI} (must be absolute and have {@code file} scheme)
     *     <li>{@link URL} (must have {@code file} protocol)
     *     <li>{@link Provider} of any of the types above
     * </ul>
     */
    public LoadFileContent fromFile(Object file) {
        var task = getTasks().register("load" + capitalize(getName()), LoadFileContent.class).get();

        if (file instanceof Buildable
            || file instanceof FileSystemLocationProperty<?>
        ) {
            task.dependsOn(file);
        }

        task.getSource().set(getObjects().fileProperty().fileProvider(getProviders().provider(() -> {
            var unwrapped = unwrapProviders(file);
            return switch (unwrapped) {
                case null -> null;
                case RegularFile it -> it.getAsFile();
                case File it -> new File(getLayout().getProjectDirectory().getAsFile(), it.getPath());
                case String it -> new File(getLayout().getProjectDirectory().getAsFile(), it);
                case URI it -> new File(it);
                case URL it -> new File(it.toURI());
                default -> throw new UnsupportedOperationException("Unsupported file type: " + unwrapped.getClass());
            };
        })));

        return task;
    }


    @Inject
    protected abstract TaskContainer getTasks();

    @Inject
    protected abstract ObjectFactory getObjects();

    @Inject
    protected abstract ProviderFactory getProviders();

    @Inject
    protected abstract ProjectLayout getLayout();

}
