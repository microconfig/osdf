package io.osdf.core.cluster.job;

import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClusterJobImpl implements ClusterJob {
    private final String name;
    private final ClusterCli cli;

    public static ClusterJobImpl clusterJob(String name, ClusterCli cli) {
        return new ClusterJobImpl(name, cli);
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
