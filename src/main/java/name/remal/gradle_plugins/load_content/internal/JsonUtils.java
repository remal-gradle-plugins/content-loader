package name.remal.gradle_plugins.load_content.internal;

import static tools.jackson.core.TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_JAVA_COMMENTS;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_MISSING_VALUES;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_SINGLE_QUOTES;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES;
import static tools.jackson.core.json.JsonReadFeature.ALLOW_YAML_COMMENTS;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES;
import static tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES;
import static tools.jackson.databind.cfg.DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS;
import static tools.jackson.databind.cfg.EnumFeature.WRITE_ENUMS_USING_TO_STRING;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import name.remal.gradle_plugins.load_content.content.json.UnexpectedJsonNodeException;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import tools.jackson.core.TSFBuilder;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ContainerNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.MissingNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

@ApiStatus.Internal
public interface JsonUtils {

    JsonFactory JSON_FACTORY = withBaseConfig(JsonFactory.builder())
        .enable(ALLOW_JAVA_COMMENTS)
        .enable(ALLOW_YAML_COMMENTS)
        .enable(ALLOW_SINGLE_QUOTES)
        .enable(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
        .enable(ALLOW_UNQUOTED_PROPERTY_NAMES)
        .enable(ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS)
        .enable(ALLOW_NON_NUMERIC_NUMBERS)
        .enable(ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS)
        .enable(ALLOW_MISSING_VALUES)
        .enable(ALLOW_TRAILING_COMMA)
        .build();

    JsonMapper JSON_MAPPER = withBaseConfig(JsonMapper.builder(JSON_FACTORY))
        .defaultPrettyPrinter(new DefaultPrettyPrinter()
            .withArrayIndenter(new DefaultIndenter("  ", "\n"))
            .withObjectIndenter(new DefaultIndenter("  ", "\n"))
        )
        .build();

    JsonNodeFactory JSON_NODES = JSON_MAPPER.getNodeFactory();

    @Contract(mutates = "param1")
    static void removeEmptyNodes(JsonNode node) {
        if (node instanceof MissingNode || node instanceof NullNode) {
            throw new UnexpectedJsonNodeException(node);

        } else if (node instanceof ArrayNode array) {
            array.forEach(JsonUtils::removeEmptyNodes);
            array.removeIf(element -> element instanceof ContainerNode<?> container && container.isEmpty());

        } else if (node instanceof ObjectNode object) {
            object.values().forEach(JsonUtils::removeEmptyNodes);
            object.values().removeIf(element -> element instanceof ContainerNode<?> container && container.isEmpty());
        }
    }

    static JsonNode evaluateJsonPath(JsonNode node, @Language("JSONPath") String jsonPath) {
        var compiledJsonPath = JsonPath.compile(jsonPath);
        var configuration = Configuration.defaultConfiguration()
            .jsonProvider(Jackson3JsonNodeJsonProvider.INSTANCE);
        return compiledJsonPath.read(node, configuration);
    }


    private static <T extends TSFBuilder<?, ?>> T withBaseConfig(T builder) {
        builder
            .disable(INTERN_PROPERTY_NAMES)
        ;
        return builder;
    }

    private static <T extends MapperBuilder<?, ?>> T withBaseConfig(T builder) {
        builder
            .enable(ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
            .enable(ACCEPT_CASE_INSENSITIVE_VALUES)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(FAIL_ON_TRAILING_TOKENS)
            .disable(READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .disable(WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .disable(WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(WRITE_ENUMS_USING_TO_STRING)
        ;

        builder.findAndAddModules(JsonUtils.class.getClassLoader());

        return builder;
    }

}
