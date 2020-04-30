package io.microconfig.osdf;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.api.v2.MainApiCaller.mainApi;
import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;
import static io.microconfig.osdf.exceptions.BugTracker.bugTracker;
import static io.microconfig.osdf.openshift.OCExecutor.oc;
import static io.microconfig.osdf.paths.OSDFPaths.paths;
import static io.microconfig.utils.Logger.error;
import static java.lang.System.exit;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
public class OpenShiftDeployStarter {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static void main(String[] args) {
        OSDFPaths paths = paths();
        try {
            new OpenShiftDeployStarter(paths, oc(paths)).run(args);
        } catch (StatusCodeException e) {
            exit(e.getStatusCode());
        } catch (OSDFException e) {
            error(e.getMessage());
            exit(1);
        } catch (Exception e) {
            error("Bug!");
            bugTracker(paths.root()).save(args.length > 0 ? args[0] : "", e);
            e.printStackTrace();
            exit(1);
        }
    }

    public void run(String[] args) {
        if (updatableCall(args)) {
            updateCommand(paths).tryAutoUpdateAndRestart(args);
        }
        mainApi(paths, oc).call(asList(args));
    }

    private boolean updatableCall(String[] args) {
        if (args.length == 0) return false;
        return !"install".equals(args[0]) && !"init".equals(args[0]) &&
                !"state".equals(args[0]) && !"update".equals(args[0]) &&
                !"howToStart".equals(args[0]) && !"help".equals(args[0]);
    }
}
