package io.microconfig.osdf.configfetcher;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.CommandLineExecutor.execute;

@RequiredArgsConstructor
public class LocalFetcher implements ConfigFetcherStrategy {
    private final Path source;

    public static LocalFetcher localFetcher(Path source) {
        return new LocalFetcher(source);
    }

    @Override
    public void fetchConfigs(String configVersion, Path destination) {
        execute("rm -rf " + destination);
        execute("cp -r " + source + " " + destination);
    }
}
