package io.osdf.core.local.configs.update.fetch.local;

import lombok.Getter;
import lombok.Setter;

import static io.microconfig.utils.Logger.error;

@Getter
@Setter
public class LocalFetcherSettings {
    private String path;
    private String version;

    public boolean verifyAndLogErrors() {
        if (path == null) {
            error("Local path to configs is not specified");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" +
                (path == null ? "" :
                        "Path: " + path + "\n") +
                (version == null ? "local" :
                        "Version: " + version + "\n");
    }
}
