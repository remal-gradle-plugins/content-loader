package name.remal.gradle_plugins.load_content.loader.file;

import static name.remal.gradle_plugins.toolkit.FileUtils.normalizeFile;
import static name.remal.gradle_plugins.toolkit.PathUtils.copyRecursively;
import static name.remal.gradle_plugins.toolkit.PathUtils.deleteRecursively;
import static org.gradle.api.tasks.PathSensitivity.RELATIVE;

import java.io.File;
import name.remal.gradle_plugins.load_content.loader.AbstractLoadSingleContent;
import name.remal.gradle_plugins.load_content.output.FileOutput;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.TaskAction;

@CacheableTask
public abstract class LoadFileContent extends AbstractLoadSingleContent {

    @InputFile
    @PathSensitive(RELATIVE)
    public abstract RegularFileProperty getSource();

    {
        getOutputFileName().convention(
            getSource()
                .map(RegularFile::getAsFile)
                .map(File::getName)
        );
    }


    @TaskAction
    public void execute() throws Throwable {
        var rootDir = normalizeFile(getRootDir().getAsFile().get());

        var sourceFile = normalizeFile(getSource().get().getAsFile());
        var content = FileContent.of(sourceFile, rootDir);

        var outputFile = normalizeFile(getOutputFile().get().getAsFile());
        deleteRecursively(outputFile.toPath());
        var output = new FileOutput(outputFile, rootDir);

        var processor = getContentProcessor().getOrNull();
        if (processor != null) {
            processor.transform(content, output);
            if (!output.isWritten()) {
                throw new IllegalStateException("The target has NOT been written: " + this);
            }

        } else {
            copyRecursively(sourceFile.toPath(), outputFile.toPath());
        }
    }

}
