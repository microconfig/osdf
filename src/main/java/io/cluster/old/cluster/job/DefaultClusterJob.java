package io.cluster.old.cluster.job;

import io.cluster.old.cluster.cli.ClusterCLI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultClusterJob implements ClusterJob {
    private final String name;
    private final ClusterCLI cli;

    public static DefaultClusterJob defaultClusterJob(String name, ClusterCLI cli) {
        return new DefaultClusterJob(name, cli);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean exists() {
        return !cli.execute("get job " + name)
                .getOutput()
                .toLowerCase()
                .contains("error");
    }

    @Override
    public void delete() {
        cli.execute("delete job " + name);
    }
}
