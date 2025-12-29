package name.remal.gradle_plugins.load_content;

import static name.remal.gradle_plugins.toolkit.ObjectUtils.doNotInline;
import static name.remal.gradle_plugins.toolkit.ProxyUtils.toDynamicInterface;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class LoadContentPlugin implements Plugin<Project> {

    public static final String LOAD_CONTENT_EXTENSION_NAME = doNotInline("loadContent");

    @Override
    public void apply(Project project) {
        var loadContentFactoriesContainer = project.getObjects().domainObjectContainer(LoadContentFactory.class);
        var loadContentExtension = toDynamicInterface(loadContentFactoriesContainer, LoadContentExtension.class);
        project.getExtensions().add(LoadContentExtension.class, LOAD_CONTENT_EXTENSION_NAME, loadContentExtension);
    }

}
