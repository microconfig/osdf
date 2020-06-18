package io.microconfig.osdf.configfetcher.git;

import io.microconfig.osdf.configfetcher.ConfigsFetcherStrategy;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.ProcessUtil.startAndWait;
import static io.microconfig.osdf.utils.YamlUtils.dump;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class GitFetcher implements ConfigsFetcherStrategy {
    private final GitFetcherSettings settings;
    private final Path settingsPath;

    public static GitFetcher gitFetcher(Path settingsPath) {
        GitFetcherSettings settings = settingsFile(GitFetcherSettings.class, settingsPath).getSettings();
        return new GitFetcher(settings, settingsPath);
    }

    @Override
    public boolean verifyAndLogErrors() {
        return settings.verifyAndLogErrors();
    }

    @Override
    public void fetch(Path destination) {
        info("Cloning " + settings.urlWithoutPassword() + " [" + settings.getBranchOrTag() + "] to " + destination);
        int status = startAndWait(
                new ProcessBuilder("git", "clone", "-b", settings.getBranchOrTag(),
                        "--single-branch", "--depth", "1", settings.getUrl(), destination.toString())
                        .inheritIO()
        );
        if (status != 0) {
            throw new OSDFException("Git repo clone error");
        }
    }

    @Override
    public void setConfigVersion(String configVersion) {
        settings.setBranchOrTag(configVersion);
        dump(settings, settingsPath);
    }

    @Override
    public String getConfigVersion() {
        return settings.getBranchOrTag();
    }

    @Override
    public String toString() {
        return "Type: git" + "\n" + settings;
    }
}
