package io.microconfig.osdf.service.deployment;

import io.microconfig.osdf.cluster.deployment.ClusterDeployment;
import io.microconfig.osdf.service.ServiceResource;
import io.microconfig.osdf.service.deployment.info.ServiceDeploymentInfo;
import io.microconfig.osdf.service.files.ServiceFiles;

public interface ServiceDeployment extends ClusterDeployment, ServiceResource {
    boolean createConfigMap(ServiceFiles files);

    ServiceDeploymentInfo info();
}
