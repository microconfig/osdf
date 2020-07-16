package io.osdf.core.cluster.deployment;

import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.DeploymentApi;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.osdf.core.cluster.deployment.ClusterDeploymentImpl.clusterDeployment;
import static io.osdf.test.cluster.DeploymentApi.deploymentApi;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ClusterDeploymentImplTest {
    @Test
    void testGetPods() {
        DeploymentApi deploymentApi = deploymentApi("deployment", "example");
        ClusterDeploymentImpl deployment = clusterDeployment("example", "deployment", deploymentApi);

        List<Pod> pods = deployment.pods();

        assertEquals(expectedPods(deploymentApi), actualPods(pods));
    }

    @Test
    void testScale() {
        DeploymentApi deploymentApi = deploymentApi("deployment", "example");
        ClusterDeploymentImpl deployment = clusterDeployment("example", "deployment", deploymentApi);

        deployment.scale(3);

        assertEquals(3, deploymentApi.pods().size());
    }

    @Test
    void testToResource() {
        ClusterDeploymentImpl deployment = clusterDeployment("example", "deployment", mock(ClusterCli.class));
        ClusterResource resource = deployment.toResource();

        assertEquals("example", resource.name());
        assertEquals("deployment", resource.kind());
    }

    @Test
    void testName() {
        ClusterDeploymentImpl deployment = clusterDeployment("example", "deployment", mock(ClusterCli.class));
        assertEquals("example", deployment.name());
    }

    private List<String> actualPods(List<Pod> pods) {
        return pods.stream()
                .map(pod -> "pod/" + pod.getName())
                .sorted()
                .collect(toUnmodifiableList());
    }

    private List<String> expectedPods(DeploymentApi deploymentApi) {
        return deploymentApi.pods().stream()
                .sorted()
                .collect(toUnmodifiableList());
    }
}