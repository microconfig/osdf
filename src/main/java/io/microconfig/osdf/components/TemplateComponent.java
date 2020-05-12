package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.utils.Logger;

import java.nio.file.Path;
import java.util.Objects;

import static io.microconfig.osdf.utils.CommandLineExecutor.executeAndReadLines;

public class TemplateComponent extends DeploymentComponent {

    public TemplateComponent(String name, String version, Path configDir, OCExecutor oc) {
        super(name, version, configDir, oc);
    }

    public void uploadTemplate() {
        executeAndReadLines("oc process -f " + configDir + "/openshift/template.yaml | oc apply -f -")
                .forEach(line -> Logger.info("oc: " + line));
    }

    public String getPodIp() {
        String command = "oc get pods --template='" +
                "{{range .items}}" +
                    "{{if .metadata.labels.deploymentconfig}}" +
                        "{{if eq .metadata.labels.deploymentconfig \"" + name + "." + version + "\"}}" +
                            "{{if eq .status.phase \"Running\"}}" +
                                "{{.status.podIP}}" +
                            "{{end}}" +
                        "{{end}}" +
                    "{{end}}" +
                "{{end}}'";

        return executeAndReadLines(command)
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
    }
}
