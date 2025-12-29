package name.remal.gradle_plugins.load_content;

import java.io.Serializable;
import name.remal.gradle_plugins.load_content.content.Content;
import name.remal.gradle_plugins.load_content.output.Output;

@FunctionalInterface
public interface ContentProcessor extends Serializable {

    void transform(Content content, Output output) throws Throwable;

}
