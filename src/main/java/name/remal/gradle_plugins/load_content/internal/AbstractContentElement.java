package name.remal.gradle_plugins.load_content.internal;

import lombok.Getter;
import name.remal.gradle_plugins.load_content.content.ContentElement;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Getter
public abstract class AbstractContentElement extends AbstractContentSource implements ContentElement {

    protected AbstractContentElement(String source, String fileName) {
        super(source, fileName);
    }

}
