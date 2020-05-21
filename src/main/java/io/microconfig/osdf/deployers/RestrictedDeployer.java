package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.resources.ConfigMapUploader.configMapUploader;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class RestrictedDeployer implements Deployer {
    private final OpenShiftCLI oc;

    public static RestrictedDeployer restrictedDeployer(OpenShiftCLI oc) {
        return new RestrictedDeployer(oc);
    }

    @Override
    public void deploy(DeploymentComponent component) {
        info("Deploying " + component.getName());
        configMapUploader(component, oc).upload();
        component.upload();
    }
}
