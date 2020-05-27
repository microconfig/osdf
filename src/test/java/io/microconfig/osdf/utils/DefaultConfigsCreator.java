package io.microconfig.osdf.utils;

import io.microconfig.osdf.configfetcher.git.GitFetcherSettings;
import io.microconfig.osdf.configfetcher.local.LocalFetcherSettings;
import io.microconfig.osdf.configfetcher.nexus.NexusFetcherSettings;
import io.microconfig.osdf.configs.ConfigsSettings;
import io.microconfig.osdf.install.UpdateSettings;
import io.microconfig.osdf.cluster.openshift.OpenShiftCredentials;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.settings.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.function.Consumer;

import static io.microconfig.osdf.common.Credentials.of;
import static io.microconfig.osdf.configs.ConfigsSource.LOCAL;
import static io.microconfig.osdf.nexus.NexusArtifact.nexusArtifact;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class DefaultConfigsCreator {
    private final OSDFPaths paths;

    public static DefaultConfigsCreator defaultConfigsCreator(OSDFPaths paths) {
        return new DefaultConfigsCreator(paths);
    }

    public void create() {
        git();
        nexus();
        local();
        openshift();
        configs();
        update();
    }

    private void git() {
        createSettingsFile(GitFetcherSettings.class, paths.settings().gitFetcher(), settings -> {
            settings.setUrl("fakeUrl");
            settings.setBranchOrTag("master");
        });
    }

    private void nexus() {
        createSettingsFile(NexusFetcherSettings.class, paths.settings().nexusFetcher(), settings -> {
            settings.setUrl("fakeUrl");
            settings.setArtifact(nexusArtifact("group", "artifact", "1.0.0", "zip"));
            settings.setCredentials(of("user:pass"));
        });
    }

    private void local() {
        createSettingsFile(LocalFetcherSettings.class, paths.settings().localFetcher(), settings -> {
            settings.setPath("fakepath");
        });
    }

    private void openshift() {
        createSettingsFile(OpenShiftCredentials.class, paths.settings().openshift(), settings -> {
            settings.setCredentials(of("user:pass"));
        });
    }

    private void configs() {
        createSettingsFile(ConfigsSettings.class, paths.settings().configs(), settings -> {
            settings.setEnv("dev");
            settings.setGroup(null);
            settings.setConfigsSource(LOCAL);
            settings.setProjectVersion(null);
        });
    }

    private void update() {
        createSettingsFile(UpdateSettings.class, paths.settings().update(), settings -> {
            settings.setCredentials(of("user:pass"));
        });

    }

    private <T> void createSettingsFile(Class<T> clazz, Path path, Consumer<? super T> setter) {
        SettingsFile<T> file = settingsFile(clazz, path);
        setter.accept(file.getSettings());
        file.save();
    }
}
