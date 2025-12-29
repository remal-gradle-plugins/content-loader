package name.remal.gradle_plugins.load_content.content.json;

import static java.util.Objects.requireNonNull;
import static name.remal.gradle_plugins.load_content.internal.JsonUtils.JSON_MAPPER;

import java.util.List;
import name.remal.gradle_plugins.load_content.content.ContentElement;
import org.intellij.lang.annotations.Language;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.BigIntegerNode;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.FloatNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.MissingNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ShortNode;
import tools.jackson.databind.node.StringNode;

public interface JsonContentElement extends ContentElement {

    JsonNode getNode();


    JsonContentElement selectByJsonPointer(String jsonPointer);

    List<JsonContentElement> selectManyByJsonPointer(String jsonPointer);


    JsonContentElement selectByJsonPath(@Language("JSONPath") String jsonPath);

    List<JsonContentElement> selectManyByJsonPath(@Language("JSONPath") String jsonPath);


    <T> T mapTo(Class<T> valueType);

    <T> T mapTo(TypeReference<T> valueType);


    @SuppressWarnings("unchecked")
    default <T> T getValue() {
        var node = getNode();
        return (T) switch (node) {
            case MissingNode __ -> throw new UnexpectedJsonNodeException(node);
            case NullNode __ -> throw new UnexpectedJsonNodeException(node);
            case BooleanNode it -> it.booleanValue();
            case StringNode it -> requireNonNull(it.stringValue());
            case BinaryNode it -> requireNonNull(it.binaryValue());
            case ShortNode it -> it.shortValue();
            case IntNode it -> it.intValue();
            case LongNode it -> it.longValue();
            case BigIntegerNode it -> requireNonNull(it.bigIntegerValue());
            case FloatNode it -> it.floatValue();
            case DoubleNode it -> it.doubleValue();
            case DecimalNode it -> requireNonNull(it.decimalValue());
            default -> requireNonNull(JSON_MAPPER.convertValue(node, Object.class), "null value");
        };
    }


    @Override
    @Language("JSON")
    default String asString() {
        return JSON_MAPPER.writer().writeValueAsString(getNode());
    }

}
