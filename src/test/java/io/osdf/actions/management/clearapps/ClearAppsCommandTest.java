package io.osdf.actions.management.clearapps;

import io.osdf.context.TestContext;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.api.ConfigMapApi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.osdf.actions.management.clearapps.ClearAppsCommand.clearAppsCommand;
import static io.osdf.context.TestContext.defaultContext;
import static io.osdf.test.cluster.ApiAggregator.apis;
import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.ServiceApi.serviceApi;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClearAppsCommandTest {
    private static final TestContext context = defaultContext();

    @BeforeAll
    static void initOsdf() {
        context.initDev();
    }

    @Test
    void oldAppsAreCleared() {
        ConfigMapApi configMapApi = configMapApi("osdf-apps")
                .setContent(Map.of("apps", "apps: [old-service]"));

        ClusterCli api = apis()
                .add(configMapApi)
                .add(serviceApi("old-service"));

        clearAppsCommand(api, context.getPaths()).clear();

        assertFalse(api.execute("get configmap old-service-osdf").ok());
    }
}