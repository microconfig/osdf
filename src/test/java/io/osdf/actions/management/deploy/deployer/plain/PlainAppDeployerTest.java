package io.osdf.actions.management.deploy.deployer.plain;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.application.service.ServiceDescription;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.ApiAggregator;
import io.osdf.test.cluster.TestCli;
import io.osdf.test.cluster.api.ConfigMapApi;
import io.osdf.test.cluster.api.DeploymentApi;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import static io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer.plainAppDeployer;
import static io.osdf.actions.management.deploy.deployer.service.ServiceDeployer.serviceDeployer;
import static io.osdf.core.application.plain.PlainApplication.plainApplication;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.DeploymentApi.deploymentApi;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static org.junit.jupiter.api.Assertions.*;

class PlainAppDeployerTest {
    private ConfigMapApi systemConfigMap;
    private DeploymentApi deployment;
    private ApplicationFiles files;

    @BeforeEach
    void createResources() {
        deployment = deploymentApi("deployment", "simple-service");
        files = applicationFilesFor("simple-service");
        systemConfigMap = configMapApi(files.name() + "-osdf");
    }

    @Test
    void testDeployNew() {
        systemConfigMap.exists(false);
        deployment.deploymentResourceApi().exists(false);

        deployAndAssertSuccess(systemConfigMap, deployment);

        assertTrue(systemConfigMap.exists());
        assertTrue(deployment.deploymentResourceApi().exists());
    }

    @Test
    void testRedeploy() {
        systemConfigMap.setContent(Map.of(
                "core", new Yaml().dump(CoreDescription.from(files)),
                "service", new Yaml().dump(ServiceDescription.from(files))
        ));
        int originalDeploymentVersion = deployment.deploymentResourceApi().resourceVersion();

        deployAndAssertSuccess(systemConfigMap, deployment);

        assertNotEquals(originalDeploymentVersion, deployment.deploymentResourceApi().resourceVersion());
    }

    @Test
    void testFaultyCli() {
        TestCli faultyCli = new TestCli();
        boolean deploy = serviceDeployer(faultyCli).deploy(serviceApplication(files, faultyCli));
        assertFalse(deploy);
    }

    private void deployAndAssertSuccess(ResourceApi systemConfigMap, DeploymentApi deployment) {
        ClusterCli cli = ApiAggregator.apis()
                .add(deployment)
                .add(systemConfigMap);
        PlainApplication service = plainApplication(files, cli);
        boolean deploy = plainAppDeployer(cli).deploy(service);
        assertTrue(deploy);
    }
}