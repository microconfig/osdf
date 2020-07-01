package unstable.io.osdf.loadtesting.configs;

import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.common.utils.PropertiesUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Stream;

import static unstable.io.osdf.ResourcesHashComputer.resourcesHashComputer;
import static io.osdf.common.utils.FileUtils.*;
import static java.nio.file.Files.list;
import static java.nio.file.Paths.get;

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
            throw new PossibleBugException("Couldn't open dir at " + templatePath, e);
        }
    }

    protected void setHashValue() {
        Path componentPath = get(jmeterComponentsPath.toString(), name);
        resourcesHashComputer(componentPath).computeAll();
    }

    protected void setHealthCheckMarker(String marker, int waitSec) {
        Path propsFilePath = get(jmeterComponentsPath.toString(),name, "process.properties");
        Properties processProperties = PropertiesUtils.loadFromPath(propsFilePath);
        processProperties.setProperty("healthcheck.marker.success", marker);
        processProperties.setProperty("mgmt.start.waitSec=", String.valueOf(waitSec));
        PropertiesUtils.dumpProperties(processProperties, propsFilePath);
    }

    protected void setConfigName(Path configFilePath, String componentName) {
        String newContent = readAll(configFilePath).replace("<CONFIG_NAME>", componentName);
        Path resultFilePath = get(jmeterComponentsPath.toString(), name,
                "resources", configFilePath.getFileName().toString());
        writeStringToFile(resultFilePath, newContent);
    }

    private void prepareConfigPathsForComponent() {
        Path componentPath = get(jmeterComponentsPath.toString(), name);
        createDirectoryIfNotExists(componentPath);
        createDirectoryIfNotExists(Path.of(componentPath + "/resources"));
        createFileIfNotExists(Path.of(componentPath + "/process.properties"));
        copyConfigFile(componentPath, "deploy.yaml");
    }

    private void copyConfigFile(Path componentPath, String configFileName) {
        Path configFilePath = get(jmeterComponentsPath.toString(), configFileName);
        copyFile(configFilePath, get(componentPath.toString(), configFileName));
    }
}
