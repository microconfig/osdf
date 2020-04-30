package io.microconfig.osdf;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.api.MainApiCaller.mainApi;
import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;
import static java.util.Arrays.asList;
import static java.util.List.of;

@RequiredArgsConstructor
public class OSDFStarter {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public void run(String[] args) {
        if (updatableCall(args)) {
            updateCommand(paths).tryAutoUpdateAndRestart(args);
        }
        mainApi(paths, oc).call(asList(args));
    }

    private boolean updatableCall(String[] args) {
        if (args.length == 0) return false;
        return of("install", "init", "state", "update", "help").stream().noneMatch(command -> command.equals(args[0]));
    }
}
