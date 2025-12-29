package name.remal.gradle_plugins.load_content.content;

import static java.nio.charset.StandardCharsets.UTF_8;
import static name.remal.gradle_plugins.toolkit.InputStreamUtils.toBufferedInputStream;

import com.google.errorprone.annotations.MustBeClosed;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.load_content.content.json.JsonContentElement;
import name.remal.gradle_plugins.load_content.content.json.JsonContentParser;

@SuppressWarnings("MustBeClosedChecker")
public interface Content extends ContentSource {

    default JsonContentElement asJson() {
        return JsonContentParser.parse(this);
    }


    @MustBeClosed
    InputStream openInputStream();

    @SneakyThrows
    default byte[] asBytes() {
        var bytesOut = new ByteArrayOutputStream();
        try (var inputStream = toBufferedInputStream(openInputStream())) {
            inputStream.transferTo(bytesOut);
        }
        return bytesOut.toByteArray();
    }


    @MustBeClosed
    default Reader openReader(Charset charset) {
        return new InputStreamReader(toBufferedInputStream(openInputStream()), charset);
    }

    @MustBeClosed
    default Reader openReader() {
        return openReader(getDefaultCharset());
    }

    default Charset getDefaultCharset() {
        return UTF_8;
    }

    @SneakyThrows
    default String asString() {
        var stringWriter = new StringWriter();
        try (var reader = openReader()) {
            reader.transferTo(stringWriter);
        }
        return stringWriter.toString();
    }

}
