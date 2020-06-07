package io.microconfig.osdf.loadtesting.jmeter.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
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
    private final boolean isJmxConfig;

    public static JmeterConfigProcessor configProcessor(Path jmeterComponentsPath, int numberOfSlaves,
                                                        Path jmeterPlanPath, boolean isJmxConfig) {
        JmeterMasterConfig masterConfig = jmeterMasterConfig(jmeterComponentsPath, jmeterPlanPath);
        List<JmeterSlaveConfig> slaveConfigs = IntStream.range(0, numberOfSlaves)
                .map(i -> i + 1)
                .mapToObj(i -> "jmeter-slave-" + i)
                .map(name -> jmeterSlaveConfig(name, jmeterComponentsPath))
                .collect(Collectors.toList());
        return new JmeterConfigProcessor(masterConfig, slaveConfigs, jmeterComponentsPath, isJmxConfig);
    }

    public void init() {
        announce("Init configs");
        masterConfig.init();
        slaveConfigs.forEach(JmeterSlaveConfig::init);
    }
}
