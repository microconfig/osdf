package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.InfoApi;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.LogsCommand;
import io.microconfig.osdf.commands.StatusCommand;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.openshift.OpenShiftCLI.oc;
import static io.microconfig.osdf.printers.ColumnPrinter.printer;

@RequiredArgsConstructor
public class InfoApiImpl implements InfoApi {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static InfoApi infoApi(OSDFPaths paths, ClusterCLI cli) {
        return new InfoApiImpl(paths, cli);
    }

    @Override
    public void logs(String component, String pod) {
        cli.login();
        new LogsCommand(paths, oc(cli)).show(component, pod);
    }

    @Override
    public void status(List<String> components, Boolean withHealthCheck) {
        cli.login();
        new StatusCommand(paths, oc(cli), printer(), withHealthCheck).run(components);
    }
}
