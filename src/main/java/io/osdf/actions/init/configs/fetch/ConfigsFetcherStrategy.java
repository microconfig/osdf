package io.osdf.actions.init.configs.fetch;

import java.nio.file.Path;

public interface ConfigsFetcherStrategy {
    boolean verifyAndLogErrors();

    void fetch(Path destination);

    void setConfigVersion(String configVersion);

    String getConfigVersion();
}
