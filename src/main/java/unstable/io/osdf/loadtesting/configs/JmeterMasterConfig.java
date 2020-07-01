package unstable.io.osdf.loadtesting.configs;

import io.osdf.common.exceptions.OSDFException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.utils.YamlUtils.*;
import static java.lang.Integer.parseInt;
import static java.nio.file.Paths.get;
import static java.util.stream.IntStream.range;

public class JmeterMasterConfig {
    private final String masterName;
    private final Path jmeterComponentsPath;
    private final Path jmeterPlanPath;
    private JmeterComponentConfig jmeterConfig;

    private static final String HEALTH_CHECK_MARKER = "Remote engines have been started";
    private static final int WAIT_SEC = 100;

    public static JmeterMasterConfig jmeterMasterConfig(Path jmeterComponentsPath, Path jmeterPlanPath) {
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
        jmeterConfig.setHealthCheckMarker(HEALTH_CHECK_MARKER, WAIT_SEC);
        setJmeterPlanInMasterTemplateConfig();
    }

    public void setJmeterPlanInMasterTemplateConfig() {
        Path configMapPath = get(jmeterComponentsPath.toString(), masterName + "/resources/configmap.yaml");
        Map<String, Object> configMap = loadFromPath(configMapPath);
        getMap(configMap, "data").put("testplan.jmx", readAll(jmeterPlanPath));
        dump(configMap, configMapPath);
    }

    public int getDuration() {
        return parseInt(findItemBy(getJmeterTestPlanXml(),"ThreadGroup","ThreadGroup.duration")
                .getTextContent());
    }

    public void setThreadGroupAttribute(String attributeName, int attributeValue) {
        Document doc = getJmeterTestPlanXml();
        findItemBy(doc, "ThreadGroup", attributeName).setTextContent(String.valueOf(attributeValue));
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, ""); 
            Transformer xformer = transformerFactory.newTransformer();
            xformer.transform(new DOMSource(doc), new StreamResult(new File(jmeterPlanPath.toString())));
        } catch (TransformerException e) {
            throw new OSDFException("Failed by setting master configs to " + jmeterPlanPath);
        }
    }

    private Document getJmeterTestPlanXml() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            builderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return builderFactory.newDocumentBuilder().parse(jmeterPlanPath.toFile());
        } catch (Exception e) {
            throw new OSDFException("Failed to parse " + jmeterPlanPath);
        }
    }

    private Node findItemBy(Document doc, String tag, String attributeName) {
        NodeList childNodes = doc.getElementsByTagName(tag).item(0).getChildNodes();
        return range(0, childNodes.getLength())
                .filter(i -> childNodes.item(i).hasAttributes())
                .mapToObj(i -> {
                    Node item = childNodes.item(i);
                    NamedNodeMap attributes = item.getAttributes();
                    Node namedItem = attributes.getNamedItem("name");
                    if (namedItem != null && namedItem.getNodeValue().equals(attributeName))
                        return item;
                    return null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(jmeterPlanPath + "not contains '" + attributeName + "' tag"));
    }
}
