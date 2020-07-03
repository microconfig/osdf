package io.microconfig.osdf.loadtesting.jmeter;

import io.microconfig.osdf.cluster.pod.Pod;
import io.microconfig.osdf.loadtesting.jmeter.configs.JmeterConfigProcessor;
import io.microconfig.osdf.loadtesting.jmeter.results.JmeterLogResult;

import java.util.ArrayList;
import java.util.List;

import static io.microconfig.osdf.loadtesting.jmeter.results.JmeterLogResult.jmeterLogResult;
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
        JmeterLogResult jmeterChecker = jmeterLogResult(duration * 2, "... end of run", "summary");
        Pod pod = masterComponent.pods()
                .stream()
                .findFirst()
                .orElseThrow();
        return jmeterChecker.getResults(pod);
    }
}
