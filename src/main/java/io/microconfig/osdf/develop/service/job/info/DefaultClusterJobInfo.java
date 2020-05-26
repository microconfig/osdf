package io.microconfig.osdf.develop.service.job.info;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.components.info.JobStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.components.info.JobStatus.*;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
public class DefaultClusterJobInfo implements ClusterJobInfo {
    private final String version;
    private final String configVersion;
    private final JobStatus status;

    private static DefaultClusterJobInfo notExecuted() {
        return new DefaultClusterJobInfo("?", "?", NOT_EXECUTED);
    }

    public static DefaultClusterJobInfo info(String name, ClusterCLI cli) {
        List<String> lines = cli.execute("get job " + name + " -o custom-columns=" +
                "failed:.status.failed," +
                "succeeded:.status.succeeded," +
                "active:.status.active," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion")
                .getOutputLines();

        if (lines.get(0).toLowerCase().contains("not found")) return notExecuted();
        String[] fields = lines.get(1).split("\\s+");


        String configVersion = fields[3];
        String version = fields[4];
        JobStatus status = UNKNOWN;
        if (valueGreaterThanZero(fields[2])) status = ACTIVE;
        else if (valueGreaterThanZero(fields[1])) status = SUCCEEDED;
        else if (valueGreaterThanZero(fields[0])) status = FAILED;

        return new DefaultClusterJobInfo(version, configVersion, status);
    }

    private static boolean valueGreaterThanZero(String value) {
        Integer integer = castToInteger(value);
        return integer != null && integer > 0;
    }

    @Override
    public String version() {
        return version;
    }

    @Override
    public String configVersion() {
        return configVersion;
    }

    @Override
    public JobStatus status() {
        return status;
    }
}
