package io.microconfig.osdf.deprecated.components.info;

import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.service.job.info.JobStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.service.job.info.JobStatus.*;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
@Getter
public class JobInfo {
    private final String configVersion;
    private final String projectVersion;
    private final JobStatus status;

    private static JobInfo notExecuted() {
        return new JobInfo("?", "?", NOT_EXECUTED);
    }

    public static JobInfo jobInfo(String name, OpenShiftCLI oc) {
        List<String> lines = oc.execute("oc get job " + name + " -o custom-columns=" +
                "failed:.status.failed," +
                "succeeded:.status.succeeded," +
                "active:.status.active," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion")
                .getOutputLines();

        if (lines.get(0).toLowerCase().contains("not found")) return notExecuted();
        String[] fields = lines.get(1).split("\\s+");


        String configVersion = fields[3];
        String projectVersion = fields[4];
        JobStatus status = UNKNOWN;
        if (valueGreaterThanZero(fields[2])) status = ACTIVE;
        else if (valueGreaterThanZero(fields[1])) status = SUCCEEDED;
        else if (valueGreaterThanZero(fields[0])) status = FAILED;

        return new JobInfo(configVersion, projectVersion, status);
    }

    private static boolean valueGreaterThanZero(String value) {
        Integer integer = castToInteger(value);
        return integer != null && integer > 0;
    }
}