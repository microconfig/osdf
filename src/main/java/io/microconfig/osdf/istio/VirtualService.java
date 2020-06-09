package io.microconfig.osdf.istio;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.istio.rules.HeaderRule;
import io.microconfig.osdf.istio.rules.MainRule;
import lombok.AllArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.istio.Destination.destination;
import static io.microconfig.osdf.istio.rules.HeaderRule.headerRule;
import static io.microconfig.osdf.utils.FileUtils.readAllFromResource;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Path.of;

@AllArgsConstructor
public class VirtualService {
    private final ClusterCLI cli;
    private final String name;

    private Map<String, Object> virtualServiceYaml;

    public static VirtualService virtualService(ClusterCLI cli, String name) {
        String yaml = cli.execute("get virtualservice " + name + " -o yaml").getOutput();
        return new VirtualService(cli, name, yaml.contains("not found") || yaml.contains("error") ? null : new Yaml().load(yaml));
    }

    public VirtualService createEmpty(String encodedVersion) {
        String project = cli.execute("oc config current-context")
                .throwExceptionIfError()
                .getOutput()
                .split("/")[0];
        String yaml = readAllFromResource("templates/virtual-service.yaml")
                .replace("${application-name}", name)
                .replace("${application-version}", encodedVersion)
                .replace("${project}", project);
        virtualServiceYaml = new Yaml().load(yaml);
        return this;
    }

    public VirtualService setWeight(String encodedVersion, int weight) {
        checkVirtualServiceIsNotEmpty();

        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.setWeight(encodedVersion, weight);

        setRules(ruleSet.toYaml());
        return this;
    }

    public VirtualService setMirror(String encodedVersion) {
        checkVirtualServiceIsNotEmpty();

        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.setMirror(encodedVersion);

        setRules(ruleSet.toYaml());
        return this;
    }

    public VirtualService setHeader(String encodedVersion) {
        checkVirtualServiceIsNotEmpty();

        HeaderRule headerRule = headerRule(destination(getHost(), encodedVersion),
                "route-version", encodedVersion);

        RuleSet ruleSet = RuleSet.from(getRules());
        ruleSet.addHeaderRule(headerRule);

        setRules(ruleSet.toYaml());
        return this;
    }

    public void deleteRules(String encodedVersion) {
        if (virtualServiceYaml == null) return;

        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.deleteSubset(encodedVersion);
        if (rule.isEmpty()) {
            delete();
            return;
        }
        ruleSet.deleteHeaderRule(encodedVersion);

        setRules(ruleSet.toYaml());
        upload();
    }

    public VirtualService setFault(Fault fault) {
        checkVirtualServiceIsNotEmpty();
        RuleSet ruleSet = RuleSet.from(getRules());
        MainRule rule = ruleSet.getMainRule();
        rule.setFault(fault);
        setRules(ruleSet.toYaml());
        return this;
    }

    public void upload() {
        Path tmpPath = of("/tmp/resource.yaml");
        dump(virtualServiceYaml, tmpPath);
        cli.execute("apply -f " + tmpPath)
                .throwExceptionIfError();
    }

    public boolean exists() {
        return virtualServiceYaml != null;
    }

    public String getTrafficStatus(String encodedVersion) {
        if (virtualServiceYaml == null) return "uniform";
        return RuleSet.from(getRules()).getTrafficStatus(encodedVersion);
    }

    public void delete() {
        cli.execute("oc delete virtualservice " + name);
    }

    private List<Object> getRules() {
        return getList(virtualServiceYaml, "spec", "http");
    }

    private void setRules(List<Object> rules) {
        Map<String, Object> spec = getMap(virtualServiceYaml, "spec");
        spec.put("http", rules);
    }

    private String getHost() {
        List<Object> hosts = getList(virtualServiceYaml, "spec", "hosts");
        return (String) hosts.get(0);
    }

    private void checkVirtualServiceIsNotEmpty() {
        if (virtualServiceYaml == null) throw new OSDFException("Virtual Service not found");
    }
}
