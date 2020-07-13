package io.osdf.actions.info.status.job;

import io.osdf.core.application.job.JobApplication;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.actions.info.status.job.JobStatus.*;
import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.core.cluster.resource.ClusterResourceImpl.clusterResource;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;

@RequiredArgsConstructor
public class JobStatusGetter {
    private final ClusterCli cli;

    public static JobStatusGetter jobStatusGetter(ClusterCli cli) {
        return new JobStatusGetter(cli);
    }

    public JobStatus statusOf(JobApplication jobApp) {
        if (!jobApp.exists()) return NOT_EXECUTED;

        ClusterResource clusterResource = clusterResource("job", jobApp.job().name());
        if (!clusterResource.exists(cli)) return NOT_EXECUTED;

        ResourceProperties properties = resourceProperties(cli, clusterResource,
                of("failed", "status.failed",
                        "succeeded", "status.succeeded",
                        "active", "status.active"));
        JobStatus status = UNKNOWN;
        if (valueGreaterThanZero(properties.get("active"))) status = ACTIVE;
        else if (valueGreaterThanZero(properties.get("succeeded"))) status = SUCCEEDED;
        else if (valueGreaterThanZero(properties.get("failed"))) status = FAILED;
        return status;
    }

    private static boolean valueGreaterThanZero(String value) {
        Integer integer = castToInteger(value);
        return integer != null && integer > 0;
    }
}
