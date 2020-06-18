package io.microconfig.osdf.loadtesting.jmeter;

import io.cluster.old.cluster.cli.ClusterCli;
import io.microconfig.osdf.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.microconfig.osdf.loadtesting.jmeter.loader.JmeterPathLoader.pathLoader;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.JmeterTestBuilder.jmeterConfigBuilder;
import static io.microconfig.osdf.service.deployment.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.service.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;

@RequiredArgsConstructor
public class JmeterPlanPathGenerator {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final String componentName;

    public static JmeterPlanPathGenerator jmeterPlanPathGenerator(OsdfPaths paths, ClusterCli cli, String componentName) {
        return new JmeterPlanPathGenerator(paths, cli, componentName);
    }

    public Path generate() {
        Path jmeterComponentsPath = pathLoader(paths, componentName).jmeterComponentsPathLoad();
        Map<String, String> routes = getCurrentRoutesMap(cli, paths);
        return jmeterConfigBuilder(jmeterComponentsPath, routes).build();
    }


    private Map<String, String> getCurrentRoutesMap(ClusterCli cli, OsdfPaths paths) {
        return serviceLoader(paths, cli).loadPacks()
                .stream()
                .filter(deployPack -> deployPack.deployment().info().availableReplicas() > 0)
                .filter(deployPack -> deployPack.deployment().info().status().equals(RUNNING))
                .map(deployPack -> deployPack.deployment().name())
                .collect(Collectors.toMap(name -> name, this::getUserServiceRoutes));
    }

    private String getUserServiceRoutes(String name) {
        String command = "oc get route " + name + " -o custom-columns=HOST:.spec.host";
        List<String> output = cli.execute(command).getOutputLines();
        if (output.get(0).toLowerCase().contains("not found"))
            throw new OSDFException("Pod " + name + " ip not found");
        return output.get(1).strip();
    }
}
