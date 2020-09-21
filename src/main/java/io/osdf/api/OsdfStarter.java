package io.osdf.api;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.api.MainApiCaller.mainApi;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
public class OsdfStarter {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public void run(String[] args) {
        mainApi(paths, cli).call(MainApi.class, asList(args));
    }
}
