package io.microconfig.osdf.openshift;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

import static java.lang.Thread.currentThread;

@RequiredArgsConstructor
@EqualsAndHashCode
public class Pod implements Comparable<Pod> {
    private static final String TMP_LOG_FILE = "/tmp/osdf_logs.log";

    @Getter
    private final String name;
    private final String componentName;
    private final OCExecutor oc;

    public static Pod pod(String name, String componentName, OCExecutor oc) {
        return new Pod(name, componentName, oc);
    }

    public static Pod fromOpenShiftNotation(String notation, String componentName, OCExecutor oc) {
        return pod(notation.split("/")[1], componentName, oc);
    }

    public void delete() {
        oc.execute("oc delete pod " + name);
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

    @Override
    public int compareTo(Pod o) {
        return name.compareTo(o.name);
    }
}
