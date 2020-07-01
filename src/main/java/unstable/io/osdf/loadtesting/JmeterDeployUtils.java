package unstable.io.osdf.loadtesting;

import io.osdf.core.cluster.pod.Pod;
import unstable.io.osdf.loadtesting.configs.JmeterConfigProcessor;
import unstable.io.osdf.loadtesting.results.JmeterLogResult;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.utils.Logger.announce;

public class JmeterDeployUtils {
    private JmeterDeployUtils() {}

    public static void setSlavesHosts(JmeterConfigProcessor jmeterConfigProcessor, List<JmeterComponent> slaveComponents) {
        List<String> slaveHosts = new ArrayList<>();
        slaveComponents.forEach(component -> slaveHosts.add(component.getServiceIp()));
        jmeterConfigProcessor.getMasterConfig().setHostsInMasterTemplateConfig(slaveHosts);
    }

    public static void deployDeployments(JmeterComponent component) {
        deployDeployments(List.of(component));
    }

    public static void deployDeployments(List<JmeterComponent> components) {
        components.forEach(component -> {
            announce("Deploy " + component.getComponentName());
            component.deploy();
        });
    }

    public static String waitResults(JmeterConfigProcessor jmeterConfigProcessor, JmeterComponent masterComponent) {
        int duration = jmeterConfigProcessor.getMasterConfig().getDuration();
        announce("Start load testing. Duration: " + duration + " sec");
        JmeterLogResult jmeterChecker = JmeterLogResult.jmeterLogResult(duration * 2, "... end of run", "summary");
        Pod pod = masterComponent.pods()
                .stream()
                .findFirst()
                .orElseThrow();
        return jmeterChecker.getResults(pod);
    }
}
