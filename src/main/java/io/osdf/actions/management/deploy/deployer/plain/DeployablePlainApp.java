package io.osdf.actions.management.deploy.deployer.plain;

import io.osdf.actions.management.deploy.deployer.Deployable;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.app.PlainAppHealthChecker.plainAppHealthChecker;
import static io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer.plainAppDeployer;

@RequiredArgsConstructor
public class DeployablePlainApp implements Deployable {
    private final PlainApplication plainApp;
    private final ClusterCli cli;

    public static DeployablePlainApp deployablePlainApp(PlainApplication plainApp, ClusterCli cli) {
        return new DeployablePlainApp(plainApp, cli);
    }

    @Override
    public String name() {
        return plainApp.name();
    }

    @Override
    public boolean deploy() {
        return plainAppDeployer(cli).deploy(plainApp);
    }

    @Override
    public boolean check() {
        return plainAppHealthChecker().check(plainApp);
    }
}
