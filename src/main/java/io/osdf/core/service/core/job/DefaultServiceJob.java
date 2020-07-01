package io.osdf.core.service.core.job;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.actions.info.info.job.JobStatus;
import io.osdf.core.cluster.job.ClusterJob;
import io.osdf.actions.info.info.job.DefaultServiceJobInfo;
import io.osdf.actions.info.info.job.ServiceJobInfo;
import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.actions.info.info.job.JobStatus.FAILED;
import static io.osdf.actions.info.info.job.JobStatus.SUCCEEDED;
import static io.osdf.core.service.core.ConfigMapUploader.configMapUploader;
import static io.osdf.core.cluster.job.DefaultClusterJob.defaultClusterJob;
import static io.osdf.common.utils.ThreadUtils.sleepSec;

@RequiredArgsConstructor
public class DefaultServiceJob implements ServiceJob {
    private static final int WAIT_LIMIT = 30;

    private final String name;
    private final String version;
    private final String serviceName;
    private final ClusterCli cli;

    private final ClusterJob job;

    public static DefaultServiceJob defaultServiceJob(String name, String version, String serviceName,
                                                      ClusterCli cli) {
        return new DefaultServiceJob(name, version, serviceName, cli, defaultClusterJob(name, cli));
    }

    @Override
    public void createConfigMap(List<Path> configs) {
        configMapUploader(cli).upload(name, configs, this);
    }

    @Override
    public boolean waitUntilCompleted() {
        int checks = 0;
        while (checks < WAIT_LIMIT) {
            JobStatus status = info().status();
            if (status == SUCCEEDED) return true;
            if (status == FAILED) return false;
            sleepSec(1);
            checks++;
            if (checks % 5 == 0) Logger.info("waiting for job " + checks + "/" + WAIT_LIMIT);
        }
        return false;
    }

    @Override
    public ServiceJobInfo info() {
        return DefaultServiceJobInfo.info(name, cli);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean exists() {
        return job.exists();
    }

    @Override
    public void delete() {
        job.delete();
        cli.execute("delete configmap " + name);
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String serviceName() {
        return serviceName;
    }
}
