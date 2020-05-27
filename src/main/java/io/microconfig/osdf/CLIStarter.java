package io.microconfig.osdf;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.cluster.context.MainClusterContext.mainClusterContext;
import static io.microconfig.osdf.exceptions.BugTracker.bugTracker;
import static io.microconfig.osdf.paths.OSDFPaths.paths;
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
}
