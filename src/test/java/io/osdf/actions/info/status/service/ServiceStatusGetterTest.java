package io.osdf.actions.info.status.service;

import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.PropertiesApi;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.info.status.service.ServiceStatus.*;
import static io.osdf.actions.info.status.service.ServiceStatusGetter.serviceStatusGetter;
import static io.osdf.test.cluster.PropertiesApi.propertiesApi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceStatusGetterTest {
    @Test
    void notFoundIfServiceDoesntExist() {
        ServiceApplication service = service(false);

        ServiceStatus serviceStatus = serviceStatusGetter(mock(ClusterCli.class)).statusOf(service);

        assertEquals(NOT_FOUND, serviceStatus);
    }

    @Test
    void notFoundIfDeploymentDoesntExist() {
        ServiceApplication service = serviceWithoutDeployment();

        ServiceStatus serviceStatus = serviceStatusGetter(mock(ClusterCli.class)).statusOf(service);

        assertEquals(NOT_FOUND, serviceStatus);
    }

    @Test
    void ifCurrentReplicasEqualsReady_thenReady() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "1")
                .add("status.replicas", "1")
                .add("status.readyReplicas", "1");
        ServiceStatus status = serviceStatusGetter(propertiesApi).statusOf(service);

        assertEquals(READY, status);
    }

    @Test
    void ifRequestedReplicas_equalsAvailable_butNotReady_thenRunning() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "1")
                .add("status.replicas", "1")
                .add("status.availableReplicas", "1");
        ServiceStatus status = serviceStatusGetter(propertiesApi).statusOf(service);

        assertEquals(RUNNING, status);
    }

    @Test
    void ifNoReplicas_thenTurnedOff() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "0")
                .add("status.replicas", "0");
        ServiceStatus status = serviceStatusGetter(propertiesApi).statusOf(service);

        assertEquals(TURNED_OFF, status);
    }

    @Test
    void ifUnavailableReplicasExist_thenNotReady() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "2")
                .add("status.replicas", "2")
                .add("status.availableReplicas", "1")
                .add("status.unavailableReplicas", "1");
        ServiceStatus status = serviceStatusGetter(propertiesApi).statusOf(service);

        assertEquals(NOT_READY, status);
    }


    private ServiceApplication serviceWithoutDeployment() {
        ClusterResource deploymentResource = mock(ClusterResource.class);
        when(deploymentResource.exists(any())).thenReturn(false);

        ClusterDeployment deployment = mock(ClusterDeployment.class);
        when(deployment.toResource()).thenReturn(deploymentResource);

        ServiceApplication service = service(true);
        when(service.deployment()).thenReturn(deployment);
        return service;
    }

    private ServiceApplication deployedService() {
        ClusterResource deploymentResource = mock(ClusterResource.class);
        when(deploymentResource.exists(any())).thenReturn(true);
        when(deploymentResource.kind()).thenReturn("kind");
        when(deploymentResource.name()).thenReturn("name");

        ClusterDeployment deployment = mock(ClusterDeployment.class);
        when(deployment.toResource()).thenReturn(deploymentResource);

        ServiceApplication service = service(true);
        when(service.deployment()).thenReturn(deployment);
        return service;
    }

    private ServiceApplication service(boolean exists) {
        ServiceApplication service = mock(ServiceApplication.class);
        when(service.exists()).thenReturn(exists);
        return service;
    }
}