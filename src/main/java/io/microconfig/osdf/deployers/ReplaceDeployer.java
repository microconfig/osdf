package io.microconfig.osdf.deployers;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.resources.TotalHashesStorage;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.checker.ImageVersionChecker.imageVersionChecker;
import static io.microconfig.osdf.istio.VirtualService.virtualService;
import static io.microconfig.osdf.resources.ConfigMapUploader.configMapUploader;
import static io.microconfig.osdf.resources.TotalHashComputer.totalHashComputer;
import static io.microconfig.osdf.resources.TotalHashesStorage.totalHashesStorage;
import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ReplaceDeployer implements Deployer {
    private final OCExecutor oc;
    private final OSDFPaths paths;

    public static ReplaceDeployer replaceDeployer(OCExecutor oc, OSDFPaths paths) {
        return new ReplaceDeployer(oc, paths);
    }

    @Override
    public void deploy(DeploymentComponent component) {
        if (totalHashIsRecent(component)) {
            if (imageVersionChecker(component, paths).isLatest()) {
                info("Up-to-date");
            } else {
                info("Restarting to pull new image");
                component.restart();
            }
            return;
        }
        info("Deploying " + component.getName());
        if (deployedOtherVersion(component)) {
            info("Deleting other versions");
            component.deleteAll();
        }
        component.deleteOldResourcesFromOpenShift();
        boolean configMapUpdated = configMapUploader(component, oc).upload();
        boolean latestConfig = uploadComponentResources(component);
        restartIfNecessary(component, configMapUpdated, latestConfig);
    }

    private boolean totalHashIsRecent(DeploymentComponent component) {
        String totalHash = totalHashComputer(component).computeHash();
        TotalHashesStorage totalHashesStorage = totalHashesStorage(oc);
        if (totalHashesStorage.contains(component.fullName(), totalHash)) return true;
        info("Hash " + totalHash + " is not found for " + component.fullName());
        totalHashesStorage.setHash(component.fullName(), totalHash);
        totalHashesStorage.save();
        return false;
    }

    private void restartIfNecessary(DeploymentComponent component, boolean configMapUpdated, boolean latestConfig) {
        if (latestConfig) {
            boolean latestImage = imageVersionChecker(component, paths).isLatest();
            if (configMapUpdated) info("Application configs were updated");
            if (!latestImage) info("Component doesn't have latest image");
            if (configMapUpdated || !latestImage) {
                info("Restarting");
                component.restart();
            } else {
                info("Up-to-date");
            }
        } else {
            info("New version deployed");
        }
    }

    private boolean uploadComponentResources(DeploymentComponent component) {
        String currentHash = component.info().getHash();
        uploadVirtualService(component);
        component.upload();
        String deployedHash = component.info().getHash();
        return deployedHash.equals(currentHash);
    }

    private boolean deployedOtherVersion(DeploymentComponent component) {
        if (!component.isIstioService()) return false;
        List<DeploymentComponent> deployedComponents = component.getDeployedComponents();
        if (deployedComponents.isEmpty()) return false;

        return deployedComponents.stream()
                .noneMatch(deployed -> deployed.getVersion().equals(component.getVersion()));
    }

    public void uploadVirtualService(DeploymentComponent component) {
        if (!component.isIstioService()) return;
        VirtualService virtualService = virtualService(oc, component);
        virtualService.createEmpty();
        virtualService.upload();
    }
}
