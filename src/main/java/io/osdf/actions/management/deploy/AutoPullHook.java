package io.osdf.actions.management.deploy;

import io.osdf.actions.init.configs.ConfigsUpdater;
import io.osdf.actions.init.configs.fetch.local.LocalFetcherSettings;
import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.common.utils.FileUtils;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.configs.ConfigsSettings;
import io.osdf.core.local.configs.ConfigsSource;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.osdf.actions.init.configs.ConfigsUpdater.configsUpdater;
import static io.osdf.common.SettingsFile.settingsFile;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.core.local.configs.ConfigsSource.LOCAL;
import static java.lang.System.getenv;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.walk;
import static java.nio.file.Path.of;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RequiredArgsConstructor
public class AutoPullHook {
    private final OsdfPaths paths;

    private final Predicate<OsdfPaths> autoPullEnableChecker;
    private final ConfigsUpdater configsUpdater;

    public static AutoPullHook autoPullHook(OsdfPaths paths, ClusterCli cli) {
        return new AutoPullHook(paths, AutoPullHook::defaultEnableChecker, configsUpdater(paths, cli));
    }

    public void tryAutoPull() {
        if (!autoPullEnableChecker.test(paths)) return;
        if (!pullNeeded()) return;
        configsUpdater
                .logoutOnBuild(false)
                .fetch();
    }

    private static boolean defaultEnableChecker(OsdfPaths paths) {
        if (!"true".equals(getenv("OSDF_AUTOPULL"))) return false;
        ConfigsSource configsSource = settingsFile(ConfigsSettings.class, paths.settings().configs())
                .getSettings()
                .getConfigsSource();
        return configsSource == LOCAL;
    }

    private boolean pullNeeded() {
        String path = settingsFile(LocalFetcherSettings.class, paths.settings().localFetcher())
                .getSettings()
                .getPath();

        String currentHash = computeRepoHash(of(path + "/repo"));
        Path previousHashPath = of(paths.tmp() + "/autoPullHash");
        String previousHash = previousHash(previousHashPath);
        writeStringToFile(previousHashPath, currentHash);

        return !currentHash.equals(previousHash);
    }

    private String previousHash(Path previousHashPath) {
        if (!exists(previousHashPath)) return "unknown";
        return readAll(previousHashPath);
    }

    private String computeRepoHash(Path repo) {
        try (Stream<Path> files = walk(repo)) {
            return md5Hex(files
                    .filter(Files::isRegularFile)
                    .sorted()
                    .map(FileUtils::readAll)
                    .collect(joining()));
        } catch (IOException e) {
            throw new PossibleBugException("IO error during hash computing for auto pull policy", e);
        }
    }
}
