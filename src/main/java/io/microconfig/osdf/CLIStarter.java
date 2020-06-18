package io.microconfig.osdf;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.exceptions.PossibleBugException;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.osdf.settings.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.cluster.old.cluster.context.MainClusterContext.mainClusterContext;
import static io.microconfig.osdf.exceptions.BugTracker.bugTracker;
import static io.osdf.settings.paths.OSDFPaths.paths;
import static io.microconfig.utils.Logger.error;
import static java.lang.System.exit;

@RequiredArgsConstructor
public class CLIStarter {
    public static void main(String[] args) {
        OSDFPaths paths = paths();
        try {
            ClusterCLI cli = mainClusterContext(paths).cli();
            new OSDFStarter(paths, cli).run(args);
        } catch (StatusCodeException e) {
            exit(e.getStatusCode());
        } catch (PossibleBugException e) {
            error(e.getMessage());
            saveException(args, paths, e);
            exit(1);
        } catch (OSDFException e) {
            error(e.getMessage());
            exit(1);
        } catch (Exception e) {
            error("Bug!");
            saveException(args, paths, e);
            e.printStackTrace();
            exit(1);
        }
    }

    private static void saveException(String[] args, OSDFPaths paths, Exception e) {
        bugTracker(paths.root()).save(args.length > 0 ? args[0] : "", e);
    }
}
