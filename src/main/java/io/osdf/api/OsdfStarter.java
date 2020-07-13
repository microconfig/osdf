package io.osdf.api;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.osdf.api.MainApiCaller.mainApi;
import static io.osdf.actions.system.update.UpdateCommand.updateCommand;
import static java.util.Arrays.asList;
import static java.util.List.of;

@RequiredArgsConstructor
public class OsdfStarter {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public void run(String[] args) {
        if (updatableCall(args)) {
            updateCommand(paths).tryAutoUpdateAndRestart(args);
        }
        mainApi(paths, cli).call(asList(args));
    }

    private boolean updatableCall(String[] args) {
        if (args.length == 0) return false;
        return of("install", "init", "state", "update", "help", "migrate").stream().noneMatch(command -> command.equals(args[0]));
    }
}
