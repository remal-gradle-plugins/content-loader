package name.remal.gradle_plugins.load_content.loader;

import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;

@CacheableTask
public abstract class AbstractLoadMultipleContent extends AbstractLoadContent {

    @Override
    @OutputDirectory
    @PathSensitive(RELATIVE)
    public abstract DirectoryProperty getOutputDir();

}
