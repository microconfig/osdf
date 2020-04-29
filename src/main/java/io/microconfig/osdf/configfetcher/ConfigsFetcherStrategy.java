package io.microconfig.osdf.configfetcher;

import java.nio.file.Path;

public interface ConfigsFetcherStrategy {
    void fetch(Path destination);

    void setConfigVersion(String configVersion);

    String getConfigVersion();
}
