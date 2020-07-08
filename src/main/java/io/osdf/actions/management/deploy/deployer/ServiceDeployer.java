package io.osdf.actions.management.deploy.deployer;

import io.osdf.core.application.service.ServiceApplication;

public interface ServiceDeployer {
    void deploy(ServiceApplication application);
}
