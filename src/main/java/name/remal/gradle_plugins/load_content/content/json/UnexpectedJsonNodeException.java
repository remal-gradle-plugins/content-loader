package name.remal.gradle_plugins.load_content.content.json;

import static java.lang.String.format;

import org.jspecify.annotations.Nullable;
import tools.jackson.databind.JsonNode;

public class UnexpectedJsonNodeException extends RuntimeException {

    public UnexpectedJsonNodeException(String message) {
        super(message);
    }

    public UnexpectedJsonNodeException(@Nullable JsonNode node) {
        super(format(
            "Unexpected JSON node: %s",
            node != null ? node.getClass().getSimpleName() : "null"
        ));
    }

}
