package name.remal.gradle_plugins.load_content.content.json;

import static java.lang.String.format;
import static name.remal.gradle_plugins.load_content.internal.JsonUtils.JSON_MAPPER;
import static name.remal.gradle_plugins.load_content.internal.JsonUtils.removeEmptyNodes;

import lombok.SneakyThrows;
import name.remal.gradle_plugins.load_content.content.Content;
import org.jetbrains.annotations.ApiStatus;
import tools.jackson.databind.JsonNode;

@ApiStatus.Internal
public interface JsonContentParser {

    @SneakyThrows
    static JsonContentElement parse(Content content) {
        final JsonNode node;
        try (var reader = content.openReader()) {
            node = JSON_MAPPER.readTree(reader);
        } catch (VirtualMachineError e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(
                format(
                    "Can't parse JSON content from %s",
                    content.getSource()
                ),
                e
            );
        }

        removeEmptyNodes(node);

        return new JsonContentElementImpl(content.getSource(), content.getFileName(), node);
    }

}
