package io.microconfig.osdf.istio;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.istio.rules.HeaderRule;
import io.microconfig.osdf.istio.rules.MainRule;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.AllArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.istio.Destination.destination;
import static io.microconfig.osdf.istio.rules.HeaderRule.headerRule;
import static io.microconfig.osdf.openshift.OpenShiftResource.uploadResource;
import static io.microconfig.osdf.utils.FileUtils.readAllFromResource;
import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.osdf.utils.YamlUtils.getMap;

@AllArgsConstructor
public class VirtualService {
    private final OCExecutor oc;
    private final DeploymentComponent component;
    private Map<String, Object> virtualService;

    public static VirtualService virtualService(OCExecutor oc, DeploymentComponent component) {
        String yaml = oc.execute("oc get virtualservice " + component.getName() + " -o yaml", true);
        return new VirtualService(oc, component, yaml.contains("not found") || yaml.contains("error") ? null : new Yaml().load(yaml));
    }

    public void createEmpty() {
        String yaml = readAllFromResource("templates/virtual-service.yaml")
                .replace("${application-name}", component.getName())
                .replace("${application-version}", component.getEncodedVersion())
                .replace("${project}", oc.project());
        virtualService = new Yaml().load(yaml);
    }

    public VirtualService setWeight(int weight) {
        if (virtualService == null) throw new RuntimeException("Virtual Service not found");

        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.setWeight(component.getEncodedVersion(), weight);

        setRules(ruleSet.toYaml());
        return this;
    }

    public VirtualService setMirror() {
        if (virtualService == null) throw new RuntimeException("Virtual Service not found");

        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.setMirror(component.getEncodedVersion());

        setRules(ruleSet.toYaml());
        return this;
    }

    public VirtualService setHeader() {
        if (virtualService == null) throw new RuntimeException("Virtual Service not found");

        HeaderRule headerRule = headerRule(destination(getHost(), component.getEncodedVersion()), "route-version", component.getEncodedVersion());

        RuleSet ruleSet = RuleSet.from(getRules());
        ruleSet.addHeaderRule(headerRule);

        setRules(ruleSet.toYaml());
        return this;
    }

    public void deleteRules() {
        if (virtualService == null) return;

        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.deleteSubset(component.getEncodedVersion());
        if (rule.isEmpty()) {
            delete();
            return;
        }
        ruleSet.deleteHeaderRule(component.getEncodedVersion());

        setRules(ruleSet.toYaml());
        upload();
    }

    public void upload() {
        uploadResource(oc, virtualService);
    }

    public boolean exists() {
        return virtualService != null;
    }

    public String getTrafficStatus() {
        if (virtualService == null) return "uniform";
        return RuleSet.from(getRules()).getTrafficStatus(component.getEncodedVersion());
    }

    public void delete() {
        oc.execute("oc delete virtualservice " + component.getName(), true);
    }

    private List<Object> getRules() {
        return getList(virtualService, "spec", "http");
    }

    private void setRules(List<Object> rules) {
        Map<String, Object> spec = getMap(virtualService, "spec");
        spec.put("http", rules);
    }

    private String getHost() {
        List<Object> hosts = getList(virtualService, "spec", "hosts");
        return (String) hosts.get(0);
    }
}
