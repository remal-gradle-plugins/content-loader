package name.remal.gradle_plugins.load_content.loader.file;

import static java.nio.file.Files.newInputStream;
import static name.remal.gradle_plugins.toolkit.FileUtils.normalizeFile;
import static name.remal.gradle_plugins.toolkit.InputStreamUtils.toBufferedInputStream;

import com.google.errorprone.annotations.MustBeClosed;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.load_content.internal.AbstractContent;
import name.remal.gradle_plugins.toolkit.EditorConfig;

class FileContent extends AbstractContent {

    public static FileContent of(File file, File projectRootDir) {
        file = normalizeFile(file);
        projectRootDir = normalizeFile(projectRootDir);
        return new FileContent(file, projectRootDir);
    }


    private final File file;
    private final EditorConfig editorConfig;

    private FileContent(File file, File projectRootDir) {
        super(file.getPath(), file.getName());
        this.file = file;
        this.editorConfig = new EditorConfig(projectRootDir);
    }

    @Override
    @MustBeClosed
    @SneakyThrows
    public InputStream openInputStream() {
        return toBufferedInputStream(newInputStream(file.toPath()));
    }

    @Override
    public Charset getDefaultCharset() {
        return editorConfig.getCharsetFor(file);
    }

}
