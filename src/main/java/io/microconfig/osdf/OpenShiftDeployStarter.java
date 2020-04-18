package io.microconfig.osdf;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.api.ApiCaller.apiCaller;
import static io.microconfig.osdf.api.OSDFApiImpl.osdfApi;
import static io.microconfig.osdf.api.OSDFApiInfo.commands;
import static io.microconfig.osdf.api.OSDFApiInfo.printCommandInfos;
import static io.microconfig.osdf.api.argsproducer.ConsoleArgs.consoleArgs;
import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;
import static io.microconfig.osdf.config.OSDFPaths.paths;
import static io.microconfig.osdf.openshift.OCExecutor.oc;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static java.util.Arrays.copyOfRange;

@RequiredArgsConstructor
public class OpenShiftDeployStarter {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static void main(String[] args) {
        OSDFPaths paths = paths();
        new OpenShiftDeployStarter(paths, getOcExecutor(paths)).run(args);
    }

    private static OCExecutor getOcExecutor(OSDFPaths paths) {
        return oc(getEnv(paths), paths.configPath());
    }

    private static String getEnv(OSDFPaths paths) {
        try {
            return fromFile(paths.stateSavePath()).getEnv();
        } catch (Exception e) {
            return null;
        }
    }

    public void run(String[] args) {
        if (badArgs(args)) return;
        if (updatableCall(args)) updateCommand(paths).tryPatchUpdateAndRestart(args);

        String command = args[0];
        String[] params = copyOfRange(args, 1, args.length);

        apiCaller(consoleArgs(params)).callCommand(osdfApi(paths, oc), command);
    }

    private boolean badArgs(String[] args) {
        List<String> commands = commands();
        if (args.length > 0 && commands.contains(args[0])) return false;
        printCommandInfos();
        return true;
    }

    private boolean updatableCall(String[] args) {
        return !"install".equals(args[0]) && !"init".equals(args[0]) &&
                !"state".equals(args[0]) && !"update".equals(args[0]) &&
                !"howToStart".equals(args[0]) && !"help".equals(args[0]);
    }
}
