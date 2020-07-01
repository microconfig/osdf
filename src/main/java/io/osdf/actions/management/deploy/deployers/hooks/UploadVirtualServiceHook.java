package io.osdf.actions.management.deploy.deployers.hooks;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.service.cluster.ClusterService;
import io.osdf.core.service.core.deployment.ServiceDeployment;
import io.osdf.core.service.core.deployment.types.istio.IstioServiceDeployment;
import io.osdf.core.service.local.ServiceFiles;
import io.osdf.core.service.cluster.types.istio.IstioService;

import static io.osdf.core.service.cluster.types.istio.IstioService.toIstioService;

public class UploadVirtualServiceHook implements DeployHook {
    public static UploadVirtualServiceHook uploadVirtualServiceHook() {
        return new UploadVirtualServiceHook();
    }

    @Override
    public void call(ClusterService service, ServiceDeployment deployment, ServiceFiles files) {
        IstioService istioService = toIstioService(service);
        IstioServiceDeployment istioDeployment = istioDeployment(deployment);

        istioService.virtualService()
                .createEmpty(istioDeployment.encodedVersion())
                .upload();
    }

    public IstioServiceDeployment istioDeployment(ServiceDeployment deployment) {
        if (deployment instanceof IstioServiceDeployment) return (IstioServiceDeployment) deployment;
        throw new OSDFException(deployment.serviceName() + " is not istio service");
    }
}
