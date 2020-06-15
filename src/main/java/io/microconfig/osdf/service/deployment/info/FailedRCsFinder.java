package io.microconfig.osdf.service.deployment.info;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class FailedRCsFinder {
    private final ClusterCLI cli;

    public static FailedRCsFinder buggedDCsFinder(ClusterCLI cli) {
        return new FailedRCsFinder(cli);
    }

    public List<String> find() {
        List<String> outputLines = cli.execute("oc get rc -o custom-columns=\"" +
                "name:.metadata.annotations.openshift\\.io/deployment-config\\.name," +
                "status:.metadata.annotations.openshift\\.io/deployment\\.phase" +
                "\"")
                .throwExceptionIfError()
                .getOutputLines();
        if (outputLines.size() <= 1) return emptyList();

        return outputLines.subList(1, outputLines.size())
                .stream()
                .map(line -> line.split("\\s+"))
                .filter(split -> split.length == 2)
                .filter(split -> split[1].contains("Failed"))
                .map(split -> split[0].strip())
                .collect(toUnmodifiableList());
    }
}
