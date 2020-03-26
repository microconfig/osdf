package io.microconfig.osdf.configfetcher;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.ProcessUtil.startAndWait;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
@Setter
public class GitFetcher implements ConfigFetcherStrategy {
    private final String url;

    public static GitFetcher gitFetcher(String url) {
        return new GitFetcher(url);
    }

    public void fetchConfigs(String tagOrBranch, Path destination) {
        info("Cloning " + urlWithoutPassword(url) + " [" + tagOrBranch + "] to " + destination);

        int status = startAndWait(
                new ProcessBuilder("git", "clone", "-b", tagOrBranch, "--single-branch", "--depth", "1", url, destination.toString())
        );

        if (status != 0) {
            throw new IllegalArgumentException("Git repo clone error");
        }
    }

    public static String urlWithoutPassword(String gitUrl) {
        return gitUrl == null ? null : gitUrl.substring(gitUrl.indexOf('@') + 1);
    }
}
