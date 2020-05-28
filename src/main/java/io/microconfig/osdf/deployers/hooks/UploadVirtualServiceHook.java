package io.microconfig.osdf.deployers.hooks;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.deployment.ServiceDeployment;
import io.microconfig.osdf.service.deployment.istio.IstioServiceDeployment;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.istio.IstioService;

import static io.microconfig.osdf.utils.IstioUtils.toIstioService;

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
