package io.osdf.core.local.configs.update.fetch;

import java.nio.file.Path;

public interface ConfigsFetcherStrategy {
    boolean verifyAndLogErrors();

    void fetch(Path destination);

    void setConfigVersion(String configVersion);

    String getConfigVersion();
}
