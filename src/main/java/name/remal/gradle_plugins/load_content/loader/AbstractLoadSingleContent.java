package name.remal.gradle_plugins.load_content.loader;

import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;

@CacheableTask
public abstract class AbstractLoadSingleContent extends AbstractLoadContent {

    @Input
    public abstract Property<String> getOutputFileName();


    private final RegularFileProperty outputFile = getObjects().fileProperty();

    {
        outputFile.convention(
            getOutputDir().file(getOutputFileName())
        );
    }

    @OutputFile
    @PathSensitive(RELATIVE)
    public Provider<RegularFile> getOutputFile() {
        return outputFile;
    }

}
