package name.remal.gradle_plugins.load_content.internal;

import static name.remal.gradle_plugins.build_time_constants.api.BuildTimeConstants.getStringProperty;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface Constants {

    String PLUGIN_ID = getStringProperty("plugin-id");

    String HTTP_CLIENT_CACHE_VERSION = getStringProperty("httpclient.cache.version");

}
