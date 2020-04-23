package io.microconfig.osdf;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.exceptions.StatusCodeException;
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
import static io.microconfig.osdf.exceptions.BugTracker.bugTracker;
import static io.microconfig.osdf.install.migrations.AllMigrations.allMigrations;
import static io.microconfig.osdf.openshift.OCExecutor.oc;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.utils.Logger.error;
import static java.lang.System.exit;
import static java.util.Arrays.copyOfRange;

@RequiredArgsConstructor
public class OpenShiftDeployStarter {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static void main(String[] args) {
        OSDFPaths paths = paths();
        applyMigrations(args, paths);
        String[] filteredArgs = filterSystemArgs(args);
        new OpenShiftDeployStarter(paths, getOcExecutor(paths)).run(filteredArgs);
    }

    private static String[] filterSystemArgs(String[] args) {
        if (args[args.length - 1].equals("-UPDATE")) {
            return copyOfRange(args, 0, args.length - 1);
        }
        return args;
    }

    private static void applyMigrations(String[] args, OSDFPaths paths) {
        if (args[args.length - 1].equals("-UPDATE")) {
            allMigrations().apply(paths);
            execute("cp " + paths.newStateSavePath() + " " + paths.oldStateSavePath());
        }
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

        try {
            apiCaller(consoleArgs(params)).callCommand(osdfApi(paths, oc), command);
        } catch (StatusCodeException e) {
            exit(e.getStatusCode());
        } catch (OSDFException e) {
            error(e.getMessage());
            exit(1);
        } catch (Exception e) {
            error("Bug!");
            bugTracker(paths.root()).save(command, e);
            e.printStackTrace();
            exit(1);
        }
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
