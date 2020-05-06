package io.microconfig.osdf.components;

import io.microconfig.osdf.components.info.JobInfo;
import io.microconfig.osdf.components.info.JobStatus;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.utils.Logger;

import java.nio.file.Path;

import static io.microconfig.osdf.components.info.JobInfo.jobInfo;
import static io.microconfig.osdf.components.info.JobStatus.FAILED;
import static io.microconfig.osdf.components.info.JobStatus.SUCCEEDED;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;

public class JobComponent extends AbstractOpenShiftComponent {
    private static final int WAIT_LIMIT = 30;

    public JobComponent(String name, String version, Path configDir, OCExecutor oc) {
        super(name, version, configDir, oc);
    }

    public boolean exists() {
        return !oc.execute("oc get job " + name)
                .getOutput()
                .toLowerCase()
                .contains("error");
    }

    public JobStatus status() {
        return jobInfo(name, oc).getStatus();
    }

    public boolean waitUntilCompleted() {
        int checks = 0;
        while (checks < WAIT_LIMIT) {
            if (status() == SUCCEEDED) return true;
            if (status() == FAILED) return false;
            sleepSec(1);
            checks++;
            if (checks % 5 == 0) Logger.info("waiting for job " + checks + "/" + WAIT_LIMIT);
        }
        return false;
    }

    public JobInfo info() {
        return jobInfo(name, oc);
    }
}
