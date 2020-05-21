package io.microconfig.osdf.openshift;

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
@EqualsAndHashCode(exclude = {"oc"})
public class Pod implements Comparable<Pod> {
    private static final String TMP_LOG_FILE = "/tmp/osdf_logs.log";

    @Getter
    private final String name;
    @Getter
    private final String componentName;
    private final OpenShiftCLI oc;

    public static Pod pod(String name, String componentName, OpenShiftCLI oc) {
        return new Pod(name, componentName, oc);
    }

    public static Pod fromOpenShiftNotation(String notation, String componentName, OpenShiftCLI oc) {
        return pod(notation.split("/")[1], componentName, oc);
    }

    public static Pod fromPods(List<Pod> pods, String podName) {
        if (pods.size() == 0) throw new OSDFException("No pods found");
        if (podName == null) return pods.get(0);

        Integer order = castToInteger(podName);
        if (order != null && order < pods.size()) return pods.get(order);

        return pods.stream()
                .filter(pod -> pod.getName().equals(podName))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Pod not found"));
    }

    public void delete() {
        oc.execute("oc delete pod " + name)
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
        List<String> outputLines = oc.execute("oc get pod " + name + " -o custom-columns=\"full:.status.containerStatuses[].imageID\"")
                .throwExceptionIfError()
                .getOutputLines();
        return outputLines.get(1).split("@")[1];
    }

    @Override
    public int compareTo(Pod o) {
        return name.compareTo(o.name);
    }
}
