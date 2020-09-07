package io.osdf.test.cluster.api;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.description.ResourceDescription;
import io.osdf.core.application.service.ServiceDescription;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;
import io.osdf.test.cluster.TestCli;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import static io.osdf.test.cluster.api.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.api.DeploymentApi.deploymentApi;
import static java.util.List.of;

@Getter
@RequiredArgsConstructor
public class ServiceApi extends TestCli {
    private final ConfigMapApi configMapApi;
    private final DeploymentApi deploymentApi;

    public static ServiceApi serviceApi(String name) {
        ConfigMapApi configMapApi = configMapApi(name + "-osdf")
                .setContent(initialContent(name));
        return new ServiceApi(configMapApi, deploymentApi("deployment", name));
    }

    private static Map<String, String> initialContent(String name) {
        CoreDescription coreDescription = new CoreDescription();
        coreDescription.setAppVersion("latest");
        coreDescription.setConfigVersion("master");
        coreDescription.setResources(of("deployment/" + name));
        String core = new Yaml().dump(coreDescription);

        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setDeployment(new ResourceDescription("deployment", name));
        String service = new Yaml().dump(serviceDescription);

        return Map.of("core", core, "service", service);
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .executor(configMapApi::execute)
                .executor(deploymentApi::execute)
                .build().execute(command);
    }

    public boolean isDeleted() {
        return !configMapApi.exists() && !deploymentApi.deploymentResourceApi().exists();
    }
}
