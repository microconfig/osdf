package io.osdf.core.cluster.deployment;

import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.core.cluster.pod.Pod.fromOpenShiftNotation;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Stream.of;

@RequiredArgsConstructor
public class DefaultClusterDeployment implements ClusterDeployment {
    private final String name;
    private final String resourceKind;
    private final ClusterCli cli;

    public static DefaultClusterDeployment defaultClusterDeployment(String name, String resourceKind, ClusterCli cli) {
        return new DefaultClusterDeployment(name, resourceKind, cli);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<Pod> pods() {
        String label = label();
        if (label == null) return emptyList();
        return cli.execute("get pods " + label + " -o name")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .filter(line -> line.length() > 0)
                .map(notation -> fromOpenShiftNotation(notation, name, cli))
                .sorted()
                .collect(toUnmodifiableList());
    }

    @Override
    public void scale(int replicas) {
        cli.execute("scale " + resourceKind + " " + name + " --replicas=" + replicas)
                .throwExceptionIfError();
    }

    @Override
    public ClusterResource toResource() {
        return new ClusterResourceImpl(resourceKind, name);
    }

    private String label() {
        String selectorKey = resourceKind.equals("deployment") ? ".spec.selector.matchLabels" : ".spec.selector" ;
        CliOutput output = cli.execute("get " + resourceKind + " " + name + " -o custom-columns=\"label:" + selectorKey + "\"");
        if (!output.ok()) return null;
        String rawLabelString = output
                .getOutputLines()
                .get(1);
        String labels = of(rawLabelString.strip()
                .substring(4, rawLabelString.length() - 1)
                .split(" "))
                .map(label -> label.split(":"))
                .map(keyValue -> keyValue[0] + " in (" + keyValue[1] + ")")
                .collect(joining(", "));
        return "-l \"" + labels + "\"";
    }
}
