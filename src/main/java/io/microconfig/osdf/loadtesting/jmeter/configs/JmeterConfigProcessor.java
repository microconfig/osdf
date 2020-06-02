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
import static io.microconfig.utils.Logger.announce;

@Getter
@RequiredArgsConstructor
public class JmeterConfigProcessor {
    private final JmeterMasterConfig masterConfig;
    private final List<JmeterSlaveConfig> slaveConfigs;
    private final Path jmeterComponentsPath;

    public static JmeterConfigProcessor jmeterConfigProcessor(Path jmeterComponentsPath, int numberOfSlaves, String configName,
                                           Map<String, String> routes) {
        JmeterMasterConfig masterConfig = jmeterMasterConfig(jmeterComponentsPath, configName, routes);
        return jmeterConfigProcessor(jmeterComponentsPath, numberOfSlaves, masterConfig);
    }

    public static JmeterConfigProcessor jmeterConfigProcessor(Path jmeterComponentsPath, int numberOfSlaves, Path jmeterPlanPath) {
        JmeterMasterConfig masterConfig = jmeterMasterConfig(jmeterComponentsPath, jmeterPlanPath);
        return jmeterConfigProcessor(jmeterComponentsPath, numberOfSlaves, masterConfig);
    }

    private static JmeterConfigProcessor jmeterConfigProcessor(Path jmeterComponentsPath, int numberOfSlaves,
                                                               JmeterMasterConfig masterConfig) {
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
