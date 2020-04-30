package io.microconfig.osdf.configfetcher.local;

import io.microconfig.osdf.configfetcher.ConfigsFetcherStrategy;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;

@RequiredArgsConstructor
public class LocalFetcher implements ConfigsFetcherStrategy {
    private final LocalFetcherSettings settings;

    public static LocalFetcher localFetcher(Path settingsPath) {
        LocalFetcherSettings settings = settingsFile(LocalFetcherSettings.class, settingsPath).getSettings();
        return new LocalFetcher(settings);
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
        throw new OSDFException("Setting configs version for local configs is not supported");
    }

    @Override
    public String getConfigVersion() {
        return "local";
    }

    @Override
    public String toString() {
        return "Type: local" + "\n" + settings;
    }
}
