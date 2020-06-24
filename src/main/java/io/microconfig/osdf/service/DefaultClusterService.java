package io.microconfig.osdf.service;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.cluster.resource.ClusterResource;
import io.microconfig.osdf.cluster.resource.ClusterResourceImpl;
import io.microconfig.osdf.cluster.resource.LocalClusterResource;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DefaultClusterService implements ClusterService {
    private final String name;
    private final String version;
    private final ClusterCLI cli;

    public static DefaultClusterService defaultClusterService(String name, String version, ClusterCLI cli) {
        return new DefaultClusterService(name, version, cli);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public List<ClusterResource> resources() {
        return cli.execute("get all,configmap " + label() + " -o name")
                .throwExceptionIfError()
                .getOutputLines()
                .stream()
                .filter(not(String::isEmpty))
                .map(ClusterResourceImpl::fromOpenShiftNotation)
                .collect(toUnmodifiableList());
    }

    @Override
    public void upload(List<LocalClusterResource> resources) {
        resources.forEach(resource -> resource.upload(cli));
    }

    @Override
    public void delete() {
        cli.execute("delete all,configmap -l \"application in (" + name + ")\"");
    }

    private String label() {
        return "-l \"application in (" + name + ")\"";
    }
}
