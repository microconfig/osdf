package io.osdf.actions.init.configs.fetch.local;

import io.osdf.actions.init.configs.fetch.ConfigsFetcherStrategy;
import io.osdf.common.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.CommandLineExecutor.execute;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class LocalFetcher implements ConfigsFetcherStrategy {
    private final SettingsFile<LocalFetcherSettings> file;

    public static LocalFetcher localFetcher(Path settingsPath) {
        return new LocalFetcher(settingsFile(LocalFetcherSettings.class, settingsPath));
    }

    @Override
    public boolean verifyAndLogErrors() {
        return file.getSettings().verifyAndLogErrors();
    }

    @Override
    public void fetch(Path destination) {
        execute("rm -rf " + destination);
        execute("cp -r " + file.getSettings().getPath() + " " + destination);
    }

    @Override
    public void setConfigVersion(String configVersion) {
        file.getSettings().setVersion(configVersion);
        file.save();
    }

    @Override
    public String getConfigVersion() {
        return requireNonNullElse(file.getSettings().getVersion(), "local");
    }

    @Override
    public String toString() {
        return "Type: local" + "\n" + file.getSettings();
    }
}
