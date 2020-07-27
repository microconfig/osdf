package io.osdf.core.cluster.pod;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.osdf.common.utils.StringUtils.castToInteger;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"cli"})
public class Pod implements Comparable<Pod> {
    private static final String TMP_LOG_FILE = "/tmp/osdf_logs.log";

    @Getter
    private final String name;
    private final ClusterCli cli;

    public static Pod pod(String name, ClusterCli cli) {
        return new Pod(name, cli);
    }

    public static Pod fromOpenShiftNotation(String notation, ClusterCli cli) {
        return pod(notation.split("/")[1], cli);
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

    public void logs(String mainContainer) {
        List<String> containers = containers();
        String container = containers.contains(mainContainer) ? mainContainer : containers.get(0);
        try {
            Process logs = new ProcessBuilder("/bin/sh", "-c", "oc logs -f " + name + " -c " + container)
                    .redirectOutput(new File(TMP_LOG_FILE))
                    .start();
            Process less = new ProcessBuilder("/bin/sh", "-c", "less -R+F " + TMP_LOG_FILE)
                    .inheritIO()
                    .start();
            less.waitFor();
            logs.destroy();
        } catch (InterruptedException | IOException e) {
            currentThread().interrupt();
            throw new OSDFException("Error processing logs", e);
        }
    }

    public boolean isReady() {
        List<String> outputLines = cli.execute("get pod " + name + " -o custom-columns=\".readiness:.status.conditions[?(@.type == \\\"Ready\\\")].status\"")
                .throwExceptionIfError()
                .getOutputLines();
        if (outputLines.size() < 2) return false;
        return outputLines.get(1).trim().equalsIgnoreCase("true");
    }

    private List<String> containers() {
        List<String> outputLines = cli.execute("get pod " + name + " -o custom-columns=\"containers:.spec.containers[].name\"")
                .throwExceptionIfError()
                .getOutputLines();
        if (outputLines.size() < 2) throw new OSDFException("No containers found for pod " + name);
        return outputLines.subList(1, outputLines.size())
                .stream()
                .map(String::trim)
                .collect(toUnmodifiableList());
    }

    @Override
    public int compareTo(Pod o) {
        return name.compareTo(o.name);
    }
}
