package name.remal.gradle_plugins.load_content.content.json;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static name.remal.gradle_plugins.load_content.internal.JsonUtils.JSON_MAPPER;
import static name.remal.gradle_plugins.load_content.internal.JsonUtils.evaluateJsonPath;

import java.util.List;
import lombok.Getter;
import name.remal.gradle_plugins.load_content.internal.AbstractContentElement;
import org.intellij.lang.annotations.Language;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.MissingNode;

@Getter
class JsonContentElementImpl extends AbstractContentElement implements JsonContentElement {

    private final JsonNode node;

    public JsonContentElementImpl(String source, String fileName, JsonNode node) {
        super(source, fileName);
        this.node = node;
    }


    @Override
    public JsonContentElement selectByJsonPointer(String jsonPointer) {
        var newSource = getSource() + " (jsonPointer: " + jsonPointer + ")";
        var newNode = getNode().at(jsonPointer);
        if (newNode instanceof MissingNode) {
            throw new UnexpectedJsonNodeException("JSONPointer expression did not select any nodes: " + newSource);
        }
        return new JsonContentElementImpl(newSource, getFileName(), newNode);
    }

    @Override
    public List<JsonContentElement> selectManyByJsonPointer(String jsonPointer) {
        var newSource = getSource() + " (jsonPointer: " + jsonPointer + ")";
        var newNode = getNode().at(jsonPointer);
        if (newNode instanceof MissingNode) {
            throw new UnexpectedJsonNodeException("JSONPointer expression did not select any nodes: " + newSource);
        } else if (newNode instanceof ArrayNode arrayNode) {
            return arrayNode.valueStream()
                .map(element -> (JsonContentElement) new JsonContentElementImpl(newSource, getFileName(), element))
                .toList();
        } else {
            return List.of(new JsonContentElementImpl(newSource, getFileName(), newNode));
        }
    }


    @Override
    public JsonContentElement selectByJsonPath(@Language("JSONPath") String jsonPath) {
        var newSource = getSource() + " (jsonPath: " + jsonPath + ")";
        var newNode = evaluateJsonPath(getNode(), jsonPath);
        if (newNode instanceof MissingNode) {
            throw new UnexpectedJsonNodeException("JSONPath expression did not select any nodes: " + newSource);
        }
        return new JsonContentElementImpl(newSource, getFileName(), newNode);
    }

    @Override
    public List<JsonContentElement> selectManyByJsonPath(@Language("JSONPath") String jsonPath) {
        var newSource = getSource() + " (jsonPath: " + jsonPath + ")";
        var newNode = evaluateJsonPath(getNode(), jsonPath);
        if (newNode instanceof MissingNode) {
            throw new UnexpectedJsonNodeException("JSONPath expression did not select any nodes: " + newSource);
        } else if (newNode instanceof ArrayNode arrayNode) {
            return arrayNode.valueStream()
                .map(element -> (JsonContentElement) new JsonContentElementImpl(newSource, getFileName(), element))
                .toList();
        } else {
            return List.of(new JsonContentElementImpl(newSource, getFileName(), newNode));
        }
    }


    @Override
    public <T> T mapTo(Class<T> valueType) {
        final T value;
        try {
            value = JSON_MAPPER.convertValue(getNode(), valueType);
        } catch (VirtualMachineError e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(
                format(
                    "Can't convert JSON content to %s: %s",
                    valueType,
                    getSource()
                ),
                e
            );
        }
        requireNonNull(value, "result value is null");
        return value;
    }

    @Override
    public <T> T mapTo(TypeReference<T> valueType) {
        final T value;
        try {
            value = JSON_MAPPER.convertValue(getNode(), valueType);
        } catch (VirtualMachineError e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(
                format(
                    "Can't convert JSON content to %s: %s",
                    valueType,
                    getSource()
                ),
                e
            );
        }
        requireNonNull(value, "result value is null");
        return value;
    }

}
