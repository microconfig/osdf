package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.TemplateComponent;
import io.microconfig.osdf.components.loader.JmeterComponentsLoader;
import io.microconfig.osdf.deployers.Deployer;
import io.microconfig.osdf.loadtesting.jmeter.JmeterConfig;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftProject;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.components.checker.SuccessfulDeploymentChecker.successfulDeploymentChecker;
import static io.microconfig.osdf.components.loader.JmeterComponentsLoader.componentsLoader;
import static io.microconfig.osdf.loadtesting.jmeter.JmeterConfig.of;
import static io.microconfig.osdf.openshift.OpenShiftProject.create;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class LoadTestCommand {
    private final OSDFPaths paths;
    private final Path jmeterPlanPath;
    private final int numberOfSlaves;
    private final OCExecutor oc;
    private final Deployer deployer;

    public void run() {
        JmeterConfig jmeterConfig = of(paths, numberOfSlaves, jmeterPlanPath);
        jmeterConfig.init();

        announce("Load components");
        JmeterComponentsLoader jmeterComponentsLoader = componentsLoader(paths, oc);
        try (OpenShiftProject ignored = create(paths, oc).connect()) {
            deployDeployments(jmeterComponentsLoader, jmeterConfig);
        }
    }

    private void deployDeployments(JmeterComponentsLoader componentsLoader, JmeterConfig jmeterConfig) {
        List<String> slaveHosts = new ArrayList<>();
        componentsLoader.loadSpecificJmeterComponents(TemplateComponent.class, true)
                .forEach(component -> {
                    announce("Deploy slaves name: " + component.fullName());
                    if (checkForDeployAndHealth(component)) deployer.deploy(component);
                    if (successfulDeploymentChecker().check(component)) {
                        slaveHosts.add(component.getPodIp());
                    } else {
                        throw new RuntimeException("Slave: " + component.fullName() + " hasn't been started");
                    }
        });

        componentsLoader.loadSpecificJmeterComponents(TemplateComponent.class, false)
                .forEach(component -> {
                    announce("Deploy master name: " + component.fullName());
                    jmeterConfig.setHostsInMasterTemplateConfig(slaveHosts);
                    deployer.deploy(component);
        });
    }

    private boolean checkForDeployAndHealth(DeploymentComponent component) {
        List<DeploymentComponent> deployedComponents = component.getDeployedComponents();
        return deployedComponents.isEmpty() || !successfulDeploymentChecker().check(component);
    }
}
