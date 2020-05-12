package io.microconfig.osdf.loadtesting.jmeter;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.utils.FileUtils;
import io.microconfig.osdf.utils.PropertiesUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.microconfig.osdf.utils.FileUtils.createDirectoryIfNotExists;
import static io.microconfig.osdf.utils.FileUtils.createFileIfNotExists;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static io.microconfig.utils.Logger.announce;

@Getter
@RequiredArgsConstructor
public class JmeterConfig {

    private final OSDFPaths paths;
    private final List<String> slaveNamesList;
    private final Path jmeterPlanPath;
    private final String masterName;

    public static JmeterConfig of(OSDFPaths paths, int numberOfSlaves, Path jmeterPlanPath) {
        String masterName = "jmeter-master";

        List<String> slaveNamesList = IntStream
                .range(0, numberOfSlaves)
                .map(i -> i + 1)
                .mapToObj(i -> "jmeter-slave-" + i)
                .collect(Collectors.toList());

        return new JmeterConfig(paths, slaveNamesList, jmeterPlanPath, masterName);
    }

    public void init() {
        announce("Init configs");
        //Master configs
        Path masterTemplatePath = Path.of(paths.jmeterPath() + "/master-template.yaml");
        prepareConfigPathsForComponent(masterName);
        setConfigNameInTemplateConfig(masterTemplatePath, masterName);
        setHealthCheckMarker(Path.of(paths.componentsPath() + "/" + masterName + "/process.properties"),
                "Remote engines have been started");
        setJmeterPlanInMasterTemplateConfig();

        //Slaves configs
        Path slaveTemplatePath = Path.of(paths.jmeterPath() + "/slave-template.yaml");
        slaveNamesList.forEach(name -> {
            prepareConfigPathsForComponent(name);
            setConfigNameInTemplateConfig(slaveTemplatePath, name);
            setHealthCheckMarker(Path.of(paths.componentsPath() + "/" + name +  "/process.properties"),
                    "Created remote object");
        });
    }

    public void setHostsInMasterTemplateConfig(List<String> slaveHosts) {
        StringBuilder hosts = new StringBuilder();
        slaveHosts.forEach(host -> hosts.append(host).append(","));
        hosts.deleteCharAt(hosts.length() - 1);

        Path templateFilePath = Path.of(paths.componentsPath() + "/" + masterName + "/openshift/template.yaml");
        Map<String, Object> templateYaml = loadFromPath(templateFilePath);
        List<Map<String, Object>> objectsList = getListOfMaps(templateYaml, "objects");
        Map<String, Object> deploymentConfig = objectsList.stream()
                .filter(map -> map.containsKey("kind") && map.get("kind").equals("DeploymentConfig"))
                .findFirst()
                .orElseThrow();
        Map<String, Object> specMap = getMap(deploymentConfig, "spec");
        Map<String, Object> templateMap = getMap(specMap, "template");
        Map<String, Object> specTemplateMap = getMap(templateMap, "spec");
        Map<String, Object> containersList = getListOfMaps(specTemplateMap, "containers").get(0);
        List<Map<String, Object>> envMapList = getListOfMaps(containersList, "env");
        Map<String, Object> configHostsEnv = envMapList.stream()
                .filter(map -> map.containsKey("name") && map.get("name").equals("SLAVE_HOSTS"))
                .findFirst()
                .orElseThrow();
        configHostsEnv.put("value", hosts.toString());

        dump(templateYaml, templateFilePath);
    }

    private void setJmeterPlanInMasterTemplateConfig() {
        Path templateFilePath = Path.of(paths.componentsPath() + "/" + masterName + "/openshift/template.yaml");
        Map<String, Object> templateYaml = loadFromPath(templateFilePath);
        List<Map<String, Object>> objectsList = getListOfMaps(templateYaml, "objects");

        String testPlanContent = FileUtils.readAll(jmeterPlanPath);
        Map<String, Object> configMap = objectsList.stream()
                .filter(map -> map.containsKey("kind") && map.get("kind").equals("ConfigMap"))
                .findFirst()
                .orElseThrow();
        Map<String, Object> data = getMap(configMap, "data");
        data.put("testplan.jmx", testPlanContent);

        dump(templateYaml, templateFilePath);
    }

    private void setHealthCheckMarker(Path propsFilePath, String marker) {
        Properties processProperties = PropertiesUtils.loadFromPath(propsFilePath);
        processProperties.setProperty("healthcheck.marker.success", marker);
        PropertiesUtils.dumpProperties(processProperties, propsFilePath);
    }

    private void prepareConfigPathsForComponent(String pathName) {
        Path componentPath = Path.of(paths.componentsPath() + "/" + pathName);
        createDirectoryIfNotExists(componentPath);
        createDirectoryIfNotExists(Path.of(componentPath + "/openshift"));

        //Copy deploy.yaml and process.properties to master and slave path
        copyConfigFile(componentPath,"deploy.yaml");
        copyConfigFile(componentPath,"process.properties");
    }

    private void copyConfigFile(Path componentPath, String configFileName) {
        Path configFilePath = Path.of(paths.jmeterPath() + "/" + configFileName);
        FileUtils.copyFile(configFilePath, Path.of(componentPath + "/" + configFileName));
    }

    private void setConfigNameInTemplateConfig(Path templatePath, String pathName) {
        Map<String, Object> templateYaml = loadFromPath(templatePath);
        List<Map<String, Object>> parameters = getListOfMaps(templateYaml, "parameters");
        Map<String, Object> configNameParameter = parameters.stream()
                .filter(map -> map.containsKey("name") && map.get("name").equals("CONFIG_NAME"))
                .findFirst()
                .orElseThrow();
        configNameParameter.put("value", pathName);
        Path newTemplatePath = Path.of(paths.componentsPath() + "/" + pathName + "/openshift/template.yaml");
        createFileIfNotExists(newTemplatePath);
        dump(templateYaml, newTemplatePath);
    }
}
