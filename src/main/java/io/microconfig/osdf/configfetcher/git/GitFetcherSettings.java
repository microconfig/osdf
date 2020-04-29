package io.microconfig.osdf.configfetcher.git;

import lombok.Getter;
import lombok.Setter;

import static io.microconfig.utils.Logger.error;

@Getter
@Setter
public class GitFetcherSettings {
    private String url;
    private String branchOrTag;

    public boolean verifyAndLogErrors() {
        if (url == null) {
            error("Git url is not specified");
            return false;
        }
        if (branchOrTag == null) {
            error("Branch or tag is not specified");
            return false;
        }
        return true;
    }
}
