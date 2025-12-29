package name.remal.gradle_plugins.load_content.internal;

import static java.lang.System.identityHashCode;

import lombok.Getter;
import name.remal.gradle_plugins.load_content.content.ContentSource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
@Getter
abstract class AbstractContentSource implements ContentSource {

    private final String source;
    private final String fileName;

    protected AbstractContentSource(String source, String fileName) {
        if (source.isEmpty()) {
            throw new IllegalArgumentException("Source must not be empty");
        }
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("File name must not be empty");
        }
        this.source = source;
        this.fileName = fileName;
    }

    @Override
    public final String toString() {
        return getSource();
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        return this == obj;
    }

    @Override
    public final int hashCode() {
        return identityHashCode(this);
    }

}
