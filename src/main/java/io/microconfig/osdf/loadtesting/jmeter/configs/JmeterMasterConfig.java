package io.microconfig.osdf.loadtesting.jmeter.configs;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterTestBuilder.jmeterConfigBuilder;
import static io.microconfig.osdf.utils.FileUtils.*;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

public class JmeterMasterConfig extends JmeterComponentConfig {
    private final String masterName;
    private final Path jmeterComponentsPath;
    private final Path jmeterPlanPath;

    public static JmeterMasterConfig jmeterMasterConfig(String componentName, Path jmeterComponentsPath, Path jmeterPlanPath) {
        return new JmeterMasterConfig(componentName, jmeterComponentsPath, jmeterPlanPath);
    }

    public static JmeterMasterConfig jmeterMasterConfig(String componentName, Path jmeterComponentsPath, Map<String, String> routes) {
        Path jmeterPlanPath = jmeterConfigBuilder(componentName, jmeterComponentsPath, routes).build();
        return new JmeterMasterConfig(componentName, jmeterComponentsPath, jmeterPlanPath);
    }

    public JmeterMasterConfig(String name, Path jmeterComponentsPath, Path jmeterPlanPath) {
        super(name, jmeterComponentsPath);
        this.masterName = name;
        this.jmeterComponentsPath = jmeterComponentsPath;
        this.jmeterPlanPath = jmeterPlanPath;
    }

    public void setHostsInMasterTemplateConfig(List<String> slaveHosts) {
        StringBuilder hosts = new StringBuilder();
        slaveHosts.forEach(host -> hosts.append(host).append(","));
        hosts.deleteCharAt(hosts.length() - 1);

        Path masterConfigFile = Path.of(jmeterComponentsPath + "/" + masterName + "/openshift/deployment.yaml");
        String newContent = readAll(masterConfigFile).replace("<SLAVE_HOSTS>", hosts.toString());
        writeStringToFile(masterConfigFile, newContent);
    }

    public int getDuration() {
        Path userTestConfigPath = Path.of(jmeterComponentsPath + "/config/jmeter-test-config.yaml");
        Map<String, Object> userConfig = loadFromPath(userTestConfigPath);
        return getInt(userConfig, "duration");
    }

    @Override
    public void init() {
        getPathsInDir(Path.of(jmeterComponentsPath + "/templates/master"))
                .forEach(path -> setConfigName(path, masterName));

        Path configMapPath = Path.of(jmeterComponentsPath + "/" + masterName + "/openshift/configmap.yaml");
        setJmeterPlanInMasterTemplateConfig(jmeterPlanPath, configMapPath);
        setHashValue();
        setHealthCheckMarker("Remote engines have been started");
    }

    private static void setJmeterPlanInMasterTemplateConfig(Path jmeterPlanPath, Path configMapPath) {
        String testPlanContent = readAll(jmeterPlanPath);
        String newContent = readAll(configMapPath).replace("<TEST_PLAN>", testPlanContent);
        writeStringToFile(configMapPath, newContent);
    }
}