package io.osdf.actions.management.deploy.deployer.service;

import io.osdf.actions.management.deploy.deployer.Deployable;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.healthcheck.app.ServiceHealthChecker.serviceHealthChecker;
import static io.osdf.actions.management.deploy.deployer.service.ServiceDeployer.serviceDeployer;


@RequiredArgsConstructor
public class DeployableService implements Deployable {
    private final ServiceApplication service;
    private final ClusterCli cli;

    public static DeployableService deployableService(ServiceApplication service, ClusterCli cli) {
        return new DeployableService(service, cli);
    }

    @Override
    public String name() {
        return service.name();
    }

    @Override
    public void deploy() {
        serviceDeployer(cli).deploy(service);
    }

    @Override
    public boolean check() {
        return serviceHealthChecker(cli).check(service);
    }
}
