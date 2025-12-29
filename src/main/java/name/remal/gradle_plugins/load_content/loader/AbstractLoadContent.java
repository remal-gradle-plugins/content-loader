package name.remal.gradle_plugins.load_content.loader;

import static java.lang.String.format;
import static name.remal.gradle_plugins.load_content.internal.Constants.PLUGIN_ID;
import static name.remal.gradle_plugins.toolkit.LayoutUtils.getRootDirOf;

import javax.inject.Inject;
import name.remal.gradle_plugins.load_content.ContentProcessor;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

@CacheableTask
public abstract class AbstractLoadContent extends DefaultTask {

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


    @Inject
    protected abstract ProviderFactory getProviders();

    @Inject
    protected abstract ProjectLayout getLayout();

    @Inject
    protected abstract ObjectFactory getObjects();

}
