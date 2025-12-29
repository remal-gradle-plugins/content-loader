package name.remal.gradle_plugins.load_content.output;

import static name.remal.gradle_plugins.toolkit.FileUtils.normalizeFile;
import static name.remal.gradle_plugins.toolkit.PathUtils.createParentDirectories;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.toolkit.EditorConfig;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FileOutput implements Output {

    private final AtomicBoolean written = new AtomicBoolean(false);

    private final File file;
    private final EditorConfig editorConfig;

    public FileOutput(File file, File projectRootDir) {
        file = normalizeFile(file);
        projectRootDir = normalizeFile(projectRootDir);
        this.file = file;
        this.editorConfig = new EditorConfig(projectRootDir);
    }

    @Override
    public boolean isWritten() {
        return written.get();
    }

    @Override
    @SneakyThrows
    public void write(byte[] bytes) {
        if (!written.compareAndSet(false, true)) {
            throw new IllegalStateException("The target has already been written: " + this);
        }
        var filePath = file.toPath();
        createParentDirectories(filePath);
        Files.write(filePath, bytes);
    }

    @Override
    public String getDefaultLineSeparator() {
        return editorConfig.getLineSeparatorFor(file);
    }

    @Override
    public Charset getDefaultCharset() {
        return editorConfig.getCharsetFor(file);
    }

    @Override
    public String toString() {
        return file.toString();
    }

}
