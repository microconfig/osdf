package io.osdf.actions.info.status.service;

import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.api.PropertiesApi;
import io.osdf.test.cluster.api.ResourceApi;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.osdf.actions.info.status.service.ServiceStatus.*;
import static io.osdf.actions.info.status.service.ServiceStatusGetter.serviceStatusGetter;
import static io.osdf.test.cluster.api.PropertiesApi.propertiesApi;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceStatusGetterTest {
    @Test
    void notFoundIfServiceDoesntExist() {
        ServiceApplication service = service(false);

        assertStatusEquals(NOT_FOUND, mock(ClusterCli.class), service);
    }

    @Test
    void notFound_IfDeploymentDoesntExist() {
        ServiceApplication service = deployedService();

        ResourceApi resourceApi = resourceApi("kind", "name").exists(false);

        assertStatusEquals(NOT_FOUND, resourceApi, service);
    }

    @Test
    void ifCurrentReplicasEqualsReady_thenReady() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "1")
                .add("status.replicas", "1")
                .add("status.readyReplicas", "1");

        assertStatusEquals(READY, propertiesApi, service);
    }

    @Test
    void ifRequestedReplicas_equalsAvailable_butNotReady_thenRunning() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "1")
                .add("status.replicas", "1")
                .add("status.availableReplicas", "1");

        assertStatusEquals(RUNNING, propertiesApi, service);
    }

    @Test
    void ifNoReplicas_thenTurnedOff() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "0")
                .add("status.replicas", "0");

        assertStatusEquals(TURNED_OFF, propertiesApi, service);
    }

    @Test
    void ifUnavailableReplicasExist_thenNotReady() {
        ServiceApplication service = deployedService();

        PropertiesApi propertiesApi = propertiesApi("kind", "name")
                .add("spec.replicas", "2")
                .add("status.replicas", "2")
                .add("status.availableReplicas", "1")
                .add("status.unavailableReplicas", "1");

        assertStatusEquals(NOT_READY, propertiesApi, service);
    }

    private ServiceApplication deployedService() {
        ClusterResource deploymentResource = mock(ClusterResource.class);
        when(deploymentResource.kind()).thenReturn("kind");
        when(deploymentResource.name()).thenReturn("name");

        ClusterDeployment deployment = mock(ClusterDeployment.class);
        when(deployment.toResource()).thenReturn(deploymentResource);

        ServiceApplication service = service(true);
        when(service.deployment()).thenReturn(Optional.of(deployment));
        return service;
    }

    private ServiceApplication service(boolean exists) {
        ServiceApplication service = mock(ServiceApplication.class);
        when(service.exists()).thenReturn(exists);
        return service;
    }

    private void assertStatusEquals(ServiceStatus expected, ClusterCli cli, ServiceApplication service) {
        assertEquals(expected, serviceStatusGetter(cli).statusOf(service));
    }
}