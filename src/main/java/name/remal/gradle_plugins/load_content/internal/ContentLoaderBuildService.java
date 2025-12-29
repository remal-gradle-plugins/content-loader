package name.remal.gradle_plugins.load_content.internal;

import java.io.Serializable;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ContentLoaderBuildService<T extends BuildServiceParameters> extends BuildService<T>, Serializable {
}
