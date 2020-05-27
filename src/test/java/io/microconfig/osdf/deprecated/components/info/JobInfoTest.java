package io.microconfig.osdf.deprecated.components.info;

import io.microconfig.osdf.deprecated.components.JobComponent;
import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;
import io.microconfig.osdf.utils.TestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static io.microconfig.osdf.commandline.CommandLineOutput.errorOutput;
import static io.microconfig.osdf.commandline.CommandLineOutput.output;
import static io.microconfig.osdf.deprecated.components.info.JobInfo.jobInfo;
import static io.microconfig.osdf.service.job.info.JobStatus.*;
import static io.microconfig.osdf.utils.MockObjects.loggedInOc;
import static io.microconfig.osdf.utils.TestContext.defaultContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class JobInfoTest {
    private final TestContext context = defaultContext();
    private final String JOB_NAME = "fakejob";
    private final String JOB_VERSION = "latest";

    private OpenShiftCLI oc;

    @BeforeEach
    void setUp() throws IOException {
        oc = loggedInOc();
        context.initDev();
    }

    @Test
    void jobSucceeded() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "failed   succeeded   active   projectVersion   configVersion" + "\n" +
                "0        1           0        latest           local"
        ));
        assertEquals(SUCCEEDED, jobInfo(component().getName(), oc).getStatus());
    }

    @Test
    void jobFailed() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "failed   succeeded   active   projectVersion   configVersion" + "\n" +
                "1        0           0        latest           local"
        ));
        assertEquals(FAILED, jobInfo(component().getName(), oc).getStatus());
    }

    @Test
    void jobActive() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "failed   succeeded   active   projectVersion   configVersion" + "\n" +
                "0        0           1        latest           local"
        ));
        assertEquals(ACTIVE, jobInfo(component().getName(), oc).getStatus());
    }

    @Test
    void jobNotExecuted() {
        when(oc.execute(infoCommand())).thenReturn(errorOutput("not found error", 1));
        assertEquals(NOT_EXECUTED, jobInfo(component().getName(), oc).getStatus());
    }

    @Test
    void badFormat() {
        when(oc.execute(infoCommand())).thenReturn(output(
                "failed   succeeded   active   projectVersion   configVersion" + "\n" +
                "<none>   <none>      <none>   latest           local"
        ));
        assertEquals(UNKNOWN, jobInfo(component().getName(), oc).getStatus());
    }

    private String infoCommand() {
        return  "oc get job " + JOB_NAME + " -o custom-columns=" +
                "failed:.status.failed," +
                "succeeded:.status.succeeded," +
                "active:.status.active," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion";
    }

    private JobComponent component() {
        return new JobComponent(JOB_NAME, JOB_VERSION, Path.of(context.getPaths().componentsPath() + "/" + JOB_NAME), oc);
    }
}