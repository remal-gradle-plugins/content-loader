package name.remal.gradle_plugins.load_content.internal;

import lombok.Getter;
import name.remal.gradle_plugins.load_content.content.Content;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Getter
public abstract class AbstractContent extends AbstractContentSource implements Content {

    protected AbstractContent(String source, String fileName) {
        super(source, fileName);
    }

}
