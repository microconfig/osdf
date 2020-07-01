package io.osdf.actions.management.deploy.deployers.hooks;

import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;

public class EmptyHook implements DeployHook {
    public static EmptyHook emptyHook() {
        return new EmptyHook();
    }

    @Override
    public void call(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        //no operation hook
    }
}
