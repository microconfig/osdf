package io.osdf.actions.init.configs.fetch.git;

import io.osdf.actions.init.configs.fetch.ConfigsFetcherStrategy;
import io.osdf.common.SettingsFile;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.utils.Logger.info;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.ProcessUtil.startAndWait;

@RequiredArgsConstructor
public class GitFetcher implements ConfigsFetcherStrategy {
    private final SettingsFile<GitFetcherSettings> file;

    public static GitFetcher gitFetcher(Path settingsPath) {
        return new GitFetcher(settingsFile(GitFetcherSettings.class, settingsPath));
    }

    @Override
    public boolean verifyAndLogErrors() {
        return file.getSettings().verifyAndLogErrors();
    }

    @Override
    public void fetch(Path destination) {
        GitFetcherSettings settings = file.getSettings();

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
        file.getSettings().setBranchOrTag(configVersion);
        file.save();
    }

    @Override
    public String getConfigVersion() {
        return file.getSettings().getBranchOrTag();
    }

    @Override
    public String toString() {
        return "Type: git" + "\n" + file.getSettings();
    }
}
