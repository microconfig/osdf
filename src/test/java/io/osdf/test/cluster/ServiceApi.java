package io.osdf.test.cluster;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.description.ResourceDescription;
import io.osdf.core.application.service.ServiceDescription;
import io.osdf.core.connection.cli.CliOutput;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import static io.osdf.test.cluster.ConfigMapApi.configMapApi;
import static io.osdf.test.cluster.DeploymentApi.deploymentApi;
import static io.osdf.test.cluster.TestCliUtils.executeUsing;
import static java.util.List.of;

@RequiredArgsConstructor
public class ServiceApi extends TestCli {
    private final String name;
    private final ConfigMapApi configMapApi;
    private final DeploymentApi deploymentApi;

    public static ServiceApi serviceApi(String name) {
        ConfigMapApi configMapApi = configMapApi(name + "-osdf").setContent(initialContent(name));
        configMapApi.ignoreOtherGets(true);

        DeploymentApi deploymentApi = deploymentApi("deployment", name);
        deploymentApi.ignoreOtherGets(true);
        return new ServiceApi(name, configMapApi, deploymentApi);
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
        return executeUsing(command, of(configMapApi::execute, deploymentApi::execute));
    }
}
