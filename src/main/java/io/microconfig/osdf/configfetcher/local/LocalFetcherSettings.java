package io.microconfig.osdf.configfetcher.local;

import lombok.Getter;
import lombok.Setter;

import static io.microconfig.utils.Logger.error;

@Getter
@Setter
public class LocalFetcherSettings {
    private String path;

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
                        "Path: " + path + "\n");
    }
}
