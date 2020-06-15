package io.microconfig.osdf.configfetcher.local;

import io.microconfig.osdf.configfetcher.ConfigsFetcherStrategy;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.YamlUtils.dump;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class LocalFetcher implements ConfigsFetcherStrategy {
    private final LocalFetcherSettings settings;
    private final Path settingsPath;

    public static LocalFetcher localFetcher(Path settingsPath) {
        LocalFetcherSettings settings = settingsFile(LocalFetcherSettings.class, settingsPath).getSettings();
        return new LocalFetcher(settings, settingsPath);
    }

    @Override
    public boolean verifyAndLogErrors() {
        return settings.verifyAndLogErrors();
    }

    @Override
    public void fetch(Path destination) {
        execute("rm -rf " + destination);
        execute("cp -r " + settings.getPath() + " " + destination);
    }

    @Override
    public void setConfigVersion(String configVersion) {
        settings.setVersion(configVersion);
        dump(settings, settingsPath);
    }

    @Override
    public String getConfigVersion() {
        return requireNonNullElse(settings.getVersion(), "local");
    }

    @Override
    public String toString() {
        return "Type: local" + "\n" + settings;
    }
}
