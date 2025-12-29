package name.remal.gradle_plugins.load_content.content;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface ContentSource {

    String getSource();

    String getFileName();


    default byte[] asBytes() {
        return asString().getBytes(UTF_8);
    }

    String asString();

}
