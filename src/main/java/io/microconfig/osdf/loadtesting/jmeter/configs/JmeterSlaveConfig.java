package io.microconfig.osdf.loadtesting.jmeter.configs;

import lombok.Getter;

import java.nio.file.Path;

import static io.microconfig.osdf.utils.FileUtils.getPathsInDir;

@Getter
public class JmeterSlaveConfig extends JmeterComponentConfig {
    private final String name;
    private final Path jmeterComponentsPath;

    public static JmeterSlaveConfig jmeterSlaveConfig(String componentName, Path jmeterComponentsPath) {
        return new JmeterSlaveConfig(componentName, jmeterComponentsPath);
    }

    public JmeterSlaveConfig(String name, Path jmeterComponentsPath) {
        super(name, jmeterComponentsPath);
        this.name = name;
        this.jmeterComponentsPath = jmeterComponentsPath;
    }

    @Override
    public void init() {
        getPathsInDir(Path.of(jmeterComponentsPath + "/templates/slave"))
                .forEach(path -> setConfigName(path, name));
        setHashValue();
        setHealthCheckMarker("Created remote object");
    }
}
