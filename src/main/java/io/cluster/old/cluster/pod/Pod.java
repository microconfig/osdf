package io.cluster.old.cluster.pod;

import io.cluster.old.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.microconfig.osdf.utils.StringUtils.castToInteger;
import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"cli"})
public class Pod implements Comparable<Pod> {
    private static final String TMP_LOG_FILE = "/tmp/osdf_logs.log";

    @Getter
    private final String name;
    @Getter
    private final String componentName;
    private final ClusterCLI cli;

    public static Pod pod(String name, String componentName, ClusterCLI cli) {
        return new Pod(name, componentName, cli);
    }

    public static Pod fromOpenShiftNotation(String notation, String componentName, ClusterCLI cli) {
        return pod(notation.split("/")[1], componentName, cli);
    }

    public static Pod fromPods(List<Pod> pods, String podName) {
        if (pods.isEmpty()) throw new OSDFException("No pods found");
        if (podName == null) return pods.get(0);

        Integer order = castToInteger(podName);
        if (order != null && order < pods.size()) return pods.get(order);

        return pods.stream()
                .filter(pod -> pod.getName().equals(podName))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Pod not found"));
    }

    public void delete() {
        cli.execute("delete pod " + name)
                .throwExceptionIfError();
    }

    public void forceDelete() {
        cli.execute("delete pod " + name + " --grace-period=0 --force")
                .throwExceptionIfError();
    }

    public void logs() {
        try {
            Process logs = new ProcessBuilder("/bin/sh", "-c", "oc logs -f " + name + " -c " + componentName)
                    .redirectOutput(new File(TMP_LOG_FILE))
                    .start();
            Process less = new ProcessBuilder("/bin/sh", "-c", "less -R+F " + TMP_LOG_FILE)
                    .inheritIO()
                    .start();
            less.waitFor();
            logs.destroy();
        } catch (InterruptedException | IOException e) {
            currentThread().interrupt();
            throw new RuntimeException("Error processing logs", e);
        }
    }

    public String imageId() {
        List<String> outputLines = cli.execute("get pod " + name + " -o custom-columns=\"full:.status.containerStatuses[].imageID\"")
                .throwExceptionIfError()
                .getOutputLines();
        if (outputLines.size() == 1 || !outputLines.get(1).contains("@")) return "unknown";
        return outputLines.get(1).split("@")[1];
    }

    public boolean checkStressContainer() {
        return cli.execute("get pod " + name + " -o jsonpath=\"{.spec.containers[*].name}\"")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .anyMatch(line -> line.contains("stress-sidecar"));
    }

    @Override
    public int compareTo(Pod o) {
        return name.compareTo(o.name);
    }
}
