package io.microconfig.osdf.service.job.info;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.job.info.JobStatus.*;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
public class DefaultServiceJobInfo implements ServiceJobInfo {
    private final String version;
    private final String configVersion;
    private final JobStatus status;

    private static DefaultServiceJobInfo notExecuted() {
        return new DefaultServiceJobInfo("?", "?", NOT_EXECUTED);
    }

    public static DefaultServiceJobInfo info(String name, ClusterCLI cli) {
        List<String> lines = cli.execute("get job " + name + " -o custom-columns=" +
                "failed:.status.failed," +
                "succeeded:.status.succeeded," +
                "active:.status.active," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion")
                .getOutputLines();
        if (lines.get(0).toLowerCase().contains("not found")) return notExecuted();
        if (lines.size() < 2) throw new OSDFException("Error querying job info: " + lines);

        String[] fields = lines.get(1).split("\\s+");

        String version = fields[3];
        String configVersion = fields[4];
        JobStatus status = UNKNOWN;
        if (valueGreaterThanZero(fields[2])) status = ACTIVE;
        else if (valueGreaterThanZero(fields[1])) status = SUCCEEDED;
        else if (valueGreaterThanZero(fields[0])) status = FAILED;

        return new DefaultServiceJobInfo(version, configVersion, status);
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
