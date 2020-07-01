package io.osdf.core.service.core.job;

import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.service.local.ServiceFiles;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.osdf.core.service.core.job.DefaultServiceJob.defaultServiceJob;
import static io.osdf.common.utils.YamlUtils.getString;
import static io.osdf.common.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class ServiceJobMatcher {
    private final ClusterCli cli;

    public static ServiceJobMatcher serviceJobMatcher(ClusterCli cli) {
        return new ServiceJobMatcher(cli);
    }

    public ServiceJob match(ServiceFiles files) {
        Map<String, Object> deploy = loadFromPath(files.getPath("deploy.yaml"));
        String version = getString(deploy, "app", "version");
        return defaultServiceJob(files.name(), version, files.name(), cli);
    }
}
