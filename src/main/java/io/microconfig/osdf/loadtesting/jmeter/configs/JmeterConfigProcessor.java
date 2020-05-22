package io.microconfig.osdf.loadtesting.jmeter.configs;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.openshift.OpenShiftCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.microconfig.osdf.components.loader.ComponentsLoaderImpl.componentsLoader;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterMasterConfig.jmeterMasterConfig;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterSlaveConfig.jmeterSlaveConfig;
import static io.microconfig.osdf.utils.FileUtils.*;
import static io.microconfig.utils.Logger.announce;
import static java.nio.file.Files.exists;

@Getter
@RequiredArgsConstructor
public class JmeterConfigProcessor {
    private final JmeterMasterConfig masterConfig;
    private final List<JmeterSlaveConfig> slaveConfigs;
    private final Path jmeterComponentsPath;

    public static JmeterConfigProcessor of(OpenShiftCLI oc, OSDFPaths paths, int numberOfSlaves, Path jmeterPlanPath) {
        String jmeterComponentsPathName = "openshift-jmeter";
        Path jmeterComponentsPath = Path.of(paths.systemComponentsPath() + "/" + jmeterComponentsPathName);
        initSystemDir(paths, jmeterComponentsPathName);

        String masterName = "jmeter-master";
        JmeterMasterConfig masterConfig = jmeterPlanPath == null ?
                jmeterMasterConfig(masterName, jmeterComponentsPath, getCurrentRoutesMap(oc, paths)) :
                jmeterMasterConfig(masterName, jmeterComponentsPath, jmeterPlanPath);

        List<JmeterSlaveConfig> slaveConfigs = IntStream.range(0, numberOfSlaves)
                .map(i -> i + 1)
                .mapToObj(i -> "jmeter-slave-" + i)
                .map(name -> jmeterSlaveConfig(name, jmeterComponentsPath))
                .collect(Collectors.toList());

        return new JmeterConfigProcessor(masterConfig, slaveConfigs, jmeterComponentsPath);
    }

    public void init() {
        announce("Init configs");
        masterConfig.init();
        slaveConfigs.forEach(JmeterSlaveConfig::init);
    }

    private static void initSystemDir(OSDFPaths paths, String jmeterComponentsPathName) {
        Path jmeterComponentsPathBefore = Path.of(paths.componentsPath() + "/" + jmeterComponentsPathName);
        Path jmeterComponentsPathAfter = Path.of(paths.systemComponentsPath() + "/" + jmeterComponentsPathName);
        if (exists(jmeterComponentsPathAfter)) return;
        if (checkJmeterPath(jmeterComponentsPathBefore)) {
            createDirectoryIfNotExists(paths.systemComponentsPath());
            copyDirectory(jmeterComponentsPathBefore, jmeterComponentsPathAfter);
            deleteDirectory(jmeterComponentsPathBefore);
        }
    }

    private static boolean checkJmeterPath(Path jmeterComponentsPath) {
        if (!exists(jmeterComponentsPath))
            throw new RuntimeException("The " + jmeterComponentsPath + "not exists");
        return true;
    }

    private static Map<String, String> getCurrentRoutesMap(OpenShiftCLI oc, OSDFPaths paths) {
        return componentsLoader(paths, null, oc)
                .load(DeploymentComponent.class)
                .stream()
                .collect(Collectors.toMap(DeploymentComponent::getName, DeploymentComponent::getRoute));
    }
}
