package io.microconfig.osdf.loadtesting.jmeter.configs;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class JmeterSlaveConfig {
    private final String name;
    private final Path jmeterComponentsPath;
    private JmeterComponentConfig jmeterConfig;

    private static final String HEALTH_CHECK_MARKER = "Created remote object";
    private static final int WAIT_SEC = 25;

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
        jmeterConfig.setHealthCheckMarker(HEALTH_CHECK_MARKER, WAIT_SEC);
    }
}
