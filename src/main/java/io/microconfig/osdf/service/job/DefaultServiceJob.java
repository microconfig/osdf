package io.microconfig.osdf.service.job;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.job.info.JobStatus;
import io.microconfig.osdf.cluster.job.ClusterJob;
import io.microconfig.osdf.service.job.info.DefaultServiceJobInfo;
import io.microconfig.osdf.service.job.info.ServiceJobInfo;
import io.microconfig.utils.Logger;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.service.job.info.JobStatus.FAILED;
import static io.microconfig.osdf.service.job.info.JobStatus.SUCCEEDED;
import static io.microconfig.osdf.cluster.configmap.DefaultConfigMapUploader.configMapUploader;
import static io.microconfig.osdf.cluster.job.DefaultClusterJob.defaultClusterJob;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;

@RequiredArgsConstructor
public class DefaultServiceJob implements ServiceJob {
    private static final int WAIT_LIMIT = 30;

    private final String name;
    private final String version;
    private final String serviceName;
    private final ClusterCLI cli;

    private final ClusterJob job;

    public static DefaultServiceJob defaultServiceJob(String name, String version, String serviceName,
                                                      ClusterCLI cli) {
        return new DefaultServiceJob(name, version, serviceName, cli, defaultClusterJob(name, cli));
    }

    @Override
    public boolean createConfigMap(List<Path> configs) {
        return configMapUploader(cli).upload(name, configs, this);
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
