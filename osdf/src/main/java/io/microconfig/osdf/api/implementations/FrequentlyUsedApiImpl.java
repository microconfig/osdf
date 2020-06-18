package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.FrequentlyUsedApi;
import io.microconfig.osdf.configs.ConfigsSettings;
import io.microconfig.osdf.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import io.microconfig.osdf.settings.SettingsFile;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.configfetcher.ConfigsFetcher.fetcher;
import static io.microconfig.osdf.configs.ConfigsUpdater.configsUpdater;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class FrequentlyUsedApiImpl implements FrequentlyUsedApi {
    private final OsdfPaths paths;

    public static FrequentlyUsedApi frequentlyUsedApi(OsdfPaths paths) {
        return new FrequentlyUsedApiImpl(paths);
    }

    @Override
    public void group(String group) {
        SettingsFile<ConfigsSettings> file = settingsFile(ConfigsSettings.class, paths.settings().configs());
        file.getSettings().setGroup("ALL".equals(group) ? null : group);
        file.save();
    }

    @Override
    public void pull() {
        configsUpdater(paths).fetch();
    }

    @Override
    public void configVersion(String configVersion) {
        ConfigsSource configsSource = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getConfigsSource();
        fetcher(configsSource, paths).setConfigVersion(configVersion);

        configsUpdater(paths).fetch();
    }
}
