package io.osdf.actions.management.deploy.deployer.plain;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.plain.PlainApplication;
import io.osdf.core.application.service.ServiceDescription;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.MicroConfigComponentDir;
import io.osdf.test.cluster.ApiAggregator;
import io.osdf.test.cluster.TestCli;
import io.osdf.test.cluster.api.ConfigMapApi;
import io.osdf.test.cluster.api.DeploymentApi;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.management.deploy.deployer.plain.PlainAppDeployer.plainAppDeployer;
import static io.osdf.actions.management.deploy.deployer.service.ServiceDeployer.serviceDeployer;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.application.plain.PlainApplication.plainApplication;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.DeploymentApi.deploymentApi;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.*;

class PlainAppDeployerTest {
    @TempDir
    Path tempDir;

    private ConfigMapApi systemConfigMap;
    private DeploymentApi deployment;
    private ApplicationFiles files;

    @BeforeEach
    void createResources() throws IOException {
        systemConfigMap = configMapApi(serviceName());
        deployment = deploymentApi("deployment", "simple-service");
        files = getApplicationFiles();
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

    private ApplicationFiles getApplicationFiles() throws IOException {
        Path serviceDir = classpathFile("components/simple-service");
        copyDirectory(serviceDir.toFile(), tempDir.toFile());

        MicroConfigComponentDir componentDir = componentDir(tempDir);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(componentDir);
        return files;
    }

    private void deployAndAssertSuccess(ResourceApi systemConfigMap, DeploymentApi deployment) {
        ClusterCli cli = ApiAggregator.apis()
                .add(deployment)
                .add(systemConfigMap);
        PlainApplication service = plainApplication(files, cli);
        boolean deploy = plainAppDeployer(cli).deploy(service);
        assertTrue(deploy);
    }

    private String serviceName() {
        return tempDir.getFileName() + "-osdf";
    }
}