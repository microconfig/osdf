package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.actions.management.deploy.smart.hash.ResourcesHashComputer;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.test.cluster.api.ServiceApi;
import org.junit.jupiter.api.Test;

import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.test.cluster.api.ServiceApi.serviceApi;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpToDateServiceCheckerTest {
    @Test
    void differentHashesCheck() {
        ServiceApi serviceApi = serviceApi("simple-service");
        ServiceApplication service = serviceApplication(applicationFilesFor("simple-service", "/simple-service"), serviceApi);
        ResourcesHashComputer resourcesHashComputer = mock(ResourcesHashComputer.class);
        when(resourcesHashComputer.computeHash(any())).thenReturn("new-hash");

        boolean isUpToDate = new UpToDateServiceChecker(serviceApi, resourcesHashComputer).check(service);
        assertFalse(isUpToDate);
    }
}