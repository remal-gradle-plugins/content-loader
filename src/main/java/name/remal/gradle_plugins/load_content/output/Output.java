package name.remal.gradle_plugins.load_content.output;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.stream.StreamSupport;
import name.remal.gradle_plugins.load_content.content.Content;
import name.remal.gradle_plugins.load_content.content.ContentElement;
import org.jspecify.annotations.Nullable;

public interface Output {

    boolean isWritten();

    void write(byte[] bytes);

    default void write(String string) {
        write(string, getDefaultCharset());
    }

    default void write(String string, Charset charset) {
        write(string.getBytes(charset));
    }

    default void write(Content content) {
        write(content.asBytes());
    }

    default void write(ContentElement contentElement) {
        write(contentElement.asBytes());
    }

    default void writeLines(Iterable<? extends @Nullable ContentElement> contentElements) {
        writeLines(contentElements, getDefaultLineSeparator());
    }

    default void writeLines(Iterable<? extends @Nullable ContentElement> contentElements, String lineSeparator) {
        writeLines(contentElements, lineSeparator, getDefaultCharset());
    }

    default void writeLines(Iterable<? extends @Nullable ContentElement> contentElements, Charset charset) {
        writeLines(contentElements, getDefaultLineSeparator(), charset);
    }

    default void writeLines(
        Iterable<? extends @Nullable ContentElement> contentElements,
        String lineSeparator,
        Charset charset
    ) {
        var text = StreamSupport.stream(contentElements.spliterator(), false)
            .filter(Objects::nonNull)
            .map(ContentElement::asString)
            .collect(joining(lineSeparator));
        write(text, charset);
    }

    default String getDefaultLineSeparator() {
        return "\n";
    }

    default Charset getDefaultCharset() {
        return UTF_8;
    }

}
