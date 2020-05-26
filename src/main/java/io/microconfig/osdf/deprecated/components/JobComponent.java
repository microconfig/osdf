package io.microconfig.osdf.deprecated.components;

import io.microconfig.osdf.deprecated.components.info.JobInfo;
import io.microconfig.osdf.service.job.info.JobStatus;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.utils.Logger;

import java.nio.file.Path;

import static io.microconfig.osdf.deprecated.components.info.JobInfo.jobInfo;
import static io.microconfig.osdf.service.job.info.JobStatus.FAILED;
import static io.microconfig.osdf.service.job.info.JobStatus.SUCCEEDED;
import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;

@Deprecated
public class JobComponent extends AbstractOpenShiftComponent {
    private static final int WAIT_LIMIT = 30;

    public JobComponent(String name, String version, Path configDir, OpenShiftCLI oc) {
        super(name, version, configDir, oc);
    }

    public boolean exists() {
        return !oc.execute("oc get job " + name)
                .getOutput()
                .toLowerCase()
                .contains("error");
    }

    @Override
    public void delete() {
        oc.execute("oc delete job " + name);
        oc.execute("oc delete configmap " + name);
    }

    @Override
    public void createConfigMap() {
        Logger.info("Creating configmap");
        var createCommand = "oc create configmap " + name + " --from-file=" + configDir;
        var labelCommand = "oc label configmap " + name + " application=" + name + " projectVersion=" + version;
        oc.execute(createCommand)
                .throwExceptionIfError()
                .consumeOutput(Logger::info);
        oc.execute(labelCommand)
                .throwExceptionIfError();
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
