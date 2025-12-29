package name.remal.gradle_plugins.load_content.internal;

import static lombok.AccessLevel.PRIVATE;

import java.nio.file.Path;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@NoArgsConstructor(access = PRIVATE)
public abstract class LockUtils {

    public static Path getLockFilePath(Path basePath) {
        return basePath.resolveSibling(basePath.getFileName() + ".lock");
    }

}
