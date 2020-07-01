package unstable.io.osdf.loadtesting;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.loadtesting.loader.JmeterPathLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static unstable.io.osdf.loadtesting.testplan.JmeterTestBuilder.jmeterConfigBuilder;
import static io.osdf.actions.info.info.deployment.DeploymentStatus.RUNNING;
import static io.osdf.core.service.core.deployment.pack.loader.DefaultServiceDeployPacksLoader.serviceLoader;

@RequiredArgsConstructor
public class JmeterPlanPathGenerator {
    private final OsdfPaths paths;
    private final ClusterCli cli;
    private final String componentName;

    public static JmeterPlanPathGenerator jmeterPlanPathGenerator(OsdfPaths paths, ClusterCli cli, String componentName) {
        return new JmeterPlanPathGenerator(paths, cli, componentName);
    }

    public Path generate() {
        Path jmeterComponentsPath = JmeterPathLoader.pathLoader(paths, componentName).jmeterComponentsPathLoad();
        Map<String, String> routes = getCurrentRoutesMap(cli, paths);
        return jmeterConfigBuilder(jmeterComponentsPath, routes).build();
    }


    public static Map<String, String> getCurrentRoutesMap(ClusterCli cli, OsdfPaths paths) {
        return serviceLoader(paths, cli).loadPacks()
                .stream()
                .filter(deployPack -> deployPack.deployment().info().availableReplicas() > 0)
                .filter(deployPack -> deployPack.deployment().info().status().equals(RUNNING))
                .map(deployPack -> deployPack.deployment().name())
                .collect(Collectors.toMap(name -> name, name -> getUserServiceRoutes(cli, name)));
    }

    public static String getUserServiceRoutes(ClusterCli cli, String name) {
        String command = "oc get route " + name + " -o custom-columns=HOST:.spec.host";
        List<String> output = cli.execute(command).getOutputLines();
        if (output.get(0).toLowerCase().contains("not found"))
            throw new OSDFException("Pod " + name + " ip not found");
        return output.get(1).strip();
    }
}
