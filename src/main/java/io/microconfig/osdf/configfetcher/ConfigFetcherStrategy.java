package io.microconfig.osdf.configfetcher;

import java.nio.file.Path;

public interface ConfigFetcherStrategy {
    void fetchConfigs(String configVersion, Path destination);
}
