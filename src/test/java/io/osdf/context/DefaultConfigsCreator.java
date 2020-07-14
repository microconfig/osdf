package io.osdf.context;

import io.osdf.actions.init.configs.fetch.git.GitFetcherSettings;
import io.osdf.actions.init.configs.fetch.local.LocalFetcherSettings;
import io.osdf.actions.init.configs.fetch.nexus.NexusFetcherSettings;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.actions.system.update.UpdateSettings;
import io.osdf.core.connection.cli.openshift.OpenShiftCredentials;
import io.osdf.settings.paths.OsdfPaths;
import io.osdf.common.SettingsFile;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.function.Consumer;

import static io.osdf.common.Credentials.of;
import static io.osdf.core.local.configs.ConfigsSource.LOCAL;
import static io.osdf.common.nexus.NexusArtifact.nexusArtifact;
import static io.osdf.common.SettingsFile.settingsFile;

@RequiredArgsConstructor
public class DefaultConfigsCreator {
    private final OsdfPaths paths;

    public static DefaultConfigsCreator defaultConfigsCreator(OsdfPaths paths) {
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
