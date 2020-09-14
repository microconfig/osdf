package io.osdf.actions.configs.requiredSecrets;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.osdf.actions.configs.requiredSecrets.RequiredAppSecrets.requiredAppSecrets;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class RequiredAppSecretsTest {

    @Test
    void testList() {
        ApplicationFiles files = applicationFilesFor("simple-service");
        ServiceApplication service = serviceApplication(files, mock(ClusterCli.class));

        List<String> secrets = requiredAppSecrets().listFor(of(service));

        assertEquals(of("simple-service-secret", "simple-service-secret-2"), secrets);
    }
}