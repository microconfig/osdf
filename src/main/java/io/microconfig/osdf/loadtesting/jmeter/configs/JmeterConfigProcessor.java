package io.microconfig.osdf.loadtesting.jmeter.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterMasterConfig.jmeterMasterConfig;
import static io.microconfig.osdf.loadtesting.jmeter.configs.JmeterSlaveConfig.jmeterSlaveConfig;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.JmeterTestBuilder.jmeterConfigBuilder;
import static io.microconfig.osdf.utils.FileUtils.createDirectoryIfNotExists;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static io.microconfig.utils.Logger.announce;
import static java.nio.file.Path.of;

@Getter
@RequiredArgsConstructor
public class JmeterConfigProcessor {
    private final JmeterMasterConfig masterConfig;
    private final List<JmeterSlaveConfig> slaveConfigs;
    private final Path jmeterComponentsPath;

    public static JmeterConfigProcessor jmeterConfigProcessor(Path jmeterComponentsPath, int numberOfSlaves,
                                                              String configName, Map<String, String> routes) {
        Path userTestConfigPath = findAndSaveTestConfig(jmeterComponentsPath, configName);
        Path jmeterPlanPath = jmeterConfigBuilder(jmeterComponentsPath, routes).build(userTestConfigPath);
        return jmeterConfigProcessor(jmeterComponentsPath, numberOfSlaves, jmeterPlanPath);
    }

    private static Path findAndSaveTestConfig(Path jmeterComponentsPath, String configName) {
        Map<String, Object> applicationYaml = loadFromPath(of(jmeterComponentsPath + "/application.yaml"));
        Map<String, Object> userPlanMap = getMap(applicationYaml, "plan");
        if (userPlanMap.containsKey(configName)) {
            Path configPath = of(jmeterComponentsPath + "/config");
            createDirectoryIfNotExists(configPath);
            Path userTestPlanPath = of(configPath + "/" + configName + ".yaml");
            dump(userPlanMap.get(configName), userTestPlanPath);
            return userTestPlanPath;
        }
        throw new RuntimeException("Test plan with name: " + configName + " not found.");
    }

    public static JmeterConfigProcessor jmeterConfigProcessor(Path jmeterComponentsPath, int numberOfSlaves, Path jmeterPlanPath) {
        JmeterMasterConfig masterConfig = jmeterMasterConfig(jmeterComponentsPath, jmeterPlanPath);
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

}
