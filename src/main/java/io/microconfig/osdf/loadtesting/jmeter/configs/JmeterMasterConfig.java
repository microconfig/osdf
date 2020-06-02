package io.microconfig.osdf.loadtesting.jmeter.configs;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.microconfig.osdf.loadtesting.jmeter.JmeterTestBuilder.jmeterConfigBuilder;
import static io.microconfig.osdf.utils.FileUtils.readAll;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static java.util.stream.IntStream.range;

public class JmeterMasterConfig {
    private final String masterName;
    private final Path jmeterComponentsPath;
    private final Path jmeterPlanPath;
    private JmeterComponentConfig jmeterConfig;

    public static JmeterMasterConfig jmeterMasterConfig(Path jmeterComponentsPath, Path jmeterPlanPath) {
        return new JmeterMasterConfig("jmeter-master", jmeterComponentsPath, jmeterPlanPath);
    }

    public static JmeterMasterConfig jmeterMasterConfig(Path jmeterComponentsPath, String configName, Map<String, String> routes) {
        Path jmeterPlanPath = jmeterConfigBuilder(jmeterComponentsPath, configName, routes).build();
        return new JmeterMasterConfig("jmeter-master", jmeterComponentsPath, jmeterPlanPath);
    }

    public JmeterMasterConfig(String name, Path jmeterComponentsPath, Path jmeterPlanPath) {
        this.masterName = name;
        this.jmeterComponentsPath = jmeterComponentsPath;
        this.jmeterPlanPath = jmeterPlanPath;
        this.jmeterConfig = new JmeterComponentConfig(name, jmeterComponentsPath);
    }

    public void setHostsInMasterTemplateConfig(List<String> slaveHosts) {
        StringBuilder hosts = new StringBuilder();
        slaveHosts.forEach(host -> hosts.append(host).append(","));
        hosts.deleteCharAt(hosts.length() - 1);

        Path masterConfigFile = Path.of(jmeterComponentsPath + "/" + masterName + "/resources/deployment.yaml");
        String newContent = readAll(masterConfigFile).replace("<SLAVE_HOSTS>", hosts.toString());
        writeStringToFile(masterConfigFile, newContent);
    }

    public void init() {
        jmeterConfig.initGeneralConfigs(Path.of(jmeterComponentsPath + "/templates/master"));
        jmeterConfig.setHealthCheckMarker("Remote engines have been started");

        Path configMapPath = Path.of(jmeterComponentsPath + "/" + masterName + "/resources/configmap.yaml");
        setJmeterPlanInMasterTemplateConfig(jmeterPlanPath, configMapPath);
    }

    private static void setJmeterPlanInMasterTemplateConfig(Path jmeterPlanPath, Path configMapPath) {
        String testPlanContent = readAll(jmeterPlanPath);
        String newContent = readAll(configMapPath).replace("<TEST_PLAN>", testPlanContent);
        writeStringToFile(configMapPath, newContent);
    }

    public int getDuration(String configName) {
        if (configName != null) {
            Path userTestConfigPath = Path.of(jmeterComponentsPath + "/config/" + configName);
            Map<String, Object> userConfig = loadFromPath(userTestConfigPath);
            return getInt(userConfig, "duration");
        } else {
            return getDurationFromXmlTestPlan();
        }
    }

    private int getDurationFromXmlTestPlan() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(jmeterPlanPath.toFile());
            NodeList childNodes = doc.getElementsByTagName("ThreadGroup").item(0).getChildNodes();
            return range(0, childNodes.getLength())
                    .filter(i -> childNodes.item(i).hasAttributes())
                    .mapToObj(i -> {
                        Node item = childNodes.item(i);
                        NamedNodeMap attributes = item.getAttributes();
                        Node namedItem = attributes.getNamedItem("name");
                        if (namedItem != null && namedItem.getNodeValue().equals("ThreadGroup.duration"))
                            return item.getTextContent();
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .map(Integer::valueOf)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(jmeterPlanPath + "not contains 'ThreadGroup.duration' tag"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse " + jmeterPlanPath);
        }
    }
}
