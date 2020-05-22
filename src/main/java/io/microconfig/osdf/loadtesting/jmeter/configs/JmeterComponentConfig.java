package io.microconfig.osdf.loadtesting.jmeter.configs;

import io.microconfig.osdf.utils.FileUtils;
import io.microconfig.osdf.utils.PropertiesUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import static io.microconfig.osdf.resources.ResourcesHashComputer.resourcesHashComputer;
import static io.microconfig.osdf.utils.FileUtils.*;
import static io.microconfig.osdf.utils.FileUtils.createDirectoryIfNotExists;
import static java.nio.file.Files.list;

public class JmeterComponentConfig {
    private final String name;
    private final Path jmeterComponentsPath;

    public JmeterComponentConfig(String name, Path jmeterComponentsPath) {
        this.name = name;
        this.jmeterComponentsPath = jmeterComponentsPath;
        prepareConfigPathsForComponent();
    }

    public void initGeneralConfigs(Path templatePath) {
        try (Stream<Path> list = list(templatePath)) {
            list.forEach(path -> setConfigName(path, name));
            setHashValue();
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't open dir at " + templatePath, e);
        }
    }

    protected void setHashValue() {
        Path componentPath = Path.of(jmeterComponentsPath + "/" + name);
        resourcesHashComputer(componentPath).computeAll();
    }

    protected void setHealthCheckMarker(String marker) {
        Path propsFilePath = Path.of(jmeterComponentsPath + "/" + name + "/process.properties");
        Properties processProperties = PropertiesUtils.loadFromPath(propsFilePath);
        processProperties.setProperty("healthcheck.marker.success", marker);
        PropertiesUtils.dumpProperties(processProperties, propsFilePath);
    }

    protected void setConfigName(Path configFilePath, String componentName) {
        String newContent = readAll(configFilePath).replace("<CONFIG_NAME>", componentName);
        Path resultFilePath = Path.of(jmeterComponentsPath + "/" + name +
                "/openshift/" + configFilePath.getFileName().toString());
        writeStringToFile(resultFilePath, newContent);
    }

    private void prepareConfigPathsForComponent() {
        Path componentPath = Path.of(jmeterComponentsPath + "/" + name);
        createDirectoryIfNotExists(componentPath);
        createDirectoryIfNotExists(Path.of(componentPath + "/openshift"));

        //Copy deploy.yaml and process.properties to master and slave path
        copyConfigFile(componentPath, "deploy.yaml");
        copyConfigFile(componentPath, "process.properties");
    }

    private void copyConfigFile(Path componentPath, String configFileName) {
        Path configFilePath = Path.of(jmeterComponentsPath + "/" + configFileName);
        FileUtils.copyFile(configFilePath, Path.of(componentPath + "/" + configFileName));
    }
}
