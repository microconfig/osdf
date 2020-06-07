package io.microconfig.osdf.loadtesting.jmeter.configs;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class JmeterSlaveConfig {
    private final String name;
    private final Path jmeterComponentsPath;
    private final String healthCheckMarker = "Created remote object";
    private final int waitSec = 25;
    private JmeterComponentConfig jmeterConfig;

    public static JmeterSlaveConfig jmeterSlaveConfig(String componentName, Path jmeterComponentsPath) {
        return new JmeterSlaveConfig(componentName, jmeterComponentsPath);
    }

    public JmeterSlaveConfig(String name, Path jmeterComponentsPath) {
        this.name = name;
        this.jmeterComponentsPath = jmeterComponentsPath;
        this.jmeterConfig = new JmeterComponentConfig(name, jmeterComponentsPath);
    }

    public void init() {
        jmeterConfig.initGeneralConfigs(Path.of(jmeterComponentsPath + "/templates/slave"));
        jmeterConfig.setHealthCheckMarker(healthCheckMarker, waitSec);
    }
}
