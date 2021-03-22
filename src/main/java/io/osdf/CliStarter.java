package io.osdf;

import io.osdf.api.OsdfStarter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.common.exceptions.StatusCodeException;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.error;
import static io.osdf.common.exceptions.tracker.BugTracker.bugTracker;
import static io.osdf.core.connection.context.MainClusterContext.mainClusterContext;
import static io.osdf.settings.paths.OsdfPaths.paths;
import static java.lang.System.exit;
import static java.lang.System.getenv;

@RequiredArgsConstructor
public class CliStarter {
    public static void main(String[] args) {
        OsdfPaths paths = paths();
        try {
            ClusterCli cli = mainClusterContext(paths).cli();
            new OsdfStarter(paths, cli).run(args);
        } catch (StatusCodeException e) {
            exit(e.getStatusCode());
        } catch (PossibleBugException e) {
            printException(e);
            saveException(args, paths, e);
            exit(1);
        } catch (OSDFException e) {
            printException(e);
            exit(1);
        } catch (Exception e) {
            error("Bug!");
            saveException(args, paths, e);
            e.printStackTrace();
            exit(1);
        }
    }

    private static void saveException(String[] args, OsdfPaths paths, Exception e) {
        bugTracker(paths.root()).save(args.length > 0 ? args[0] : "", e);
    }

    private static void printException(Exception e) {
        if ("true".equals(getenv("OSDF_STACKTRACE"))) {
            e.printStackTrace();
        }
        error(e.getMessage() != null ? e.getMessage() : e.getCause().getMessage());
    }
}
