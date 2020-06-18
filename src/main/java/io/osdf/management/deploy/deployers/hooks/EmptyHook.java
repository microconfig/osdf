package io.osdf.management.deploy.deployers.hooks;

import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;

public class EmptyHook implements DeployHook {
    public static EmptyHook emptyHook() {
        return new EmptyHook();
    }

    @Override
    public void call(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        //no operation hook
    }
}
