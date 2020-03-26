package io.microconfig.osdf;

import io.microconfig.osdf.config.OSDFPaths;

import java.util.List;

import static io.microconfig.osdf.api.ApiCaller.apiCaller;
import static io.microconfig.osdf.api.OSDFApiImpl.osdfApi;
import static io.microconfig.osdf.api.OSDFApiInfo.commands;
import static io.microconfig.osdf.api.OSDFApiInfo.printCommandInfos;
import static io.microconfig.osdf.api.argsproducer.ConsoleArgs.consoleArgs;
import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;
import static io.microconfig.osdf.config.OSDFPaths.paths;
import static io.microconfig.osdf.openshift.OCExecutor.oc;
import static java.util.Arrays.copyOfRange;

public class OpenShiftDeployStarter {
    public static void main(String[] args) {
        OSDFPaths paths = paths();
        if (badArgs(args)) return;
        if (updatableCall(args)) updateCommand(paths).tryPatchUpdateAndRestart(args);

        String command = args[0];
        String[] params = copyOfRange(args, 1, args.length);

        apiCaller(consoleArgs(params)).callCommand(osdfApi(paths, oc()), command);
    }

    private static boolean badArgs(String[] args) {
        List<String> commands = commands();
        if (args.length > 0 && commands.contains(args[0])) return false;
        printCommandInfos();
        return true;
    }

    private static boolean updatableCall(String[] args) {
        return !"install".equals(args[0]) && !"init".equals(args[0]) && !"state".equals(args[0]) && !"update".equals(args[0]) && !"howToStart".equals(args[0]);
    }
}
