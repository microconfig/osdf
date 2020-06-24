package io.microconfig.osdf.service.job.matchers;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.service.files.ServiceFiles;
import io.microconfig.osdf.service.job.ServiceJob;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.microconfig.osdf.service.job.DefaultServiceJob.defaultServiceJob;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

@RequiredArgsConstructor
public class ServiceJobMatcher {
    private final ClusterCLI cli;

    public static ServiceJobMatcher serviceJobMatcher(ClusterCLI cli) {
        return new ServiceJobMatcher(cli);
    }

    public ServiceJob match(ServiceFiles files) {
        Map<String, Object> deploy = loadFromPath(files.getPath("deploy.yaml"));
        String version = getString(deploy, "app", "version");
        return defaultServiceJob(files.name(), version, files.name(), cli);
    }
}
