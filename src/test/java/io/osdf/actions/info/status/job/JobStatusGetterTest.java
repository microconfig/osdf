package io.osdf.actions.info.status.job;

import io.osdf.core.application.job.JobApplication;
import io.osdf.core.cluster.job.ClusterJob;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.api.PropertiesApi;
import org.junit.jupiter.api.Test;

import static io.osdf.actions.info.status.job.JobStatus.*;
import static io.osdf.actions.info.status.job.JobStatusGetter.jobStatusGetter;
import static io.osdf.test.cluster.api.PropertiesApi.propertiesApi;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JobStatusGetterTest {
    @Test
    void notExecuted_IfJobDoesntExist() {
        JobApplication jobApp = mock(JobApplication.class);
        when(jobApp.exists()).thenReturn(false);

        assertJobStatus(NOT_EXECUTED, mock(ClusterCli.class), jobApp);
    }

    @Test
    void notExecuted_IfJobResourceDoesntExist() {
        JobApplication jobApp = jobWithoutResource();
        assertJobStatus(NOT_EXECUTED, mock(ClusterCli.class), jobApp);
    }

    @Test
    void testActive() {
        JobApplication jobApp = deployedJob();

        PropertiesApi propertiesApi = propertiesApi("job", "example")
                .add("status.active", "1");

        assertJobStatus(ACTIVE, propertiesApi, jobApp);
    }

    @Test
    void testFailed() {
        JobApplication jobApp = deployedJob();

        PropertiesApi propertiesApi = propertiesApi("job", "example")
                .add("status.failed", "1");

        assertJobStatus(FAILED, propertiesApi, jobApp);
    }

    @Test
    void testSucceeded() {
        JobApplication jobApp = deployedJob();

        PropertiesApi propertiesApi = propertiesApi("job", "example")
                .add("status.succeeded", "1");

        assertJobStatus(SUCCEEDED, propertiesApi, jobApp);
    }

    private JobApplication jobWithoutResource() {
        ClusterResource jobResource = mock(ClusterResource.class);
        when(jobResource.exists(any())).thenReturn(false);

        ClusterJob job = mock(ClusterJob.class);
        when(job.toResource()).thenReturn(jobResource);

        JobApplication jobApp = mock(JobApplication.class);
        when(jobApp.exists()).thenReturn(true);
        when(jobApp.job()).thenReturn(job);
        return jobApp;
    }

    private JobApplication deployedJob() {
        ClusterResource jobResource = mock(ClusterResource.class);
        when(jobResource.exists(any())).thenReturn(true);
        when(jobResource.kind()).thenReturn("job");
        when(jobResource.name()).thenReturn("example");

        ClusterJob job = mock(ClusterJob.class);
        when(job.toResource()).thenReturn(jobResource);

        JobApplication jobApp = mock(JobApplication.class);
        when(jobApp.exists()).thenReturn(true);
        when(jobApp.job()).thenReturn(job);
        return jobApp;
    }


    void assertJobStatus(JobStatus expectedStatus, ClusterCli cli, JobApplication jobApp) {
        JobStatus actualStatus = jobStatusGetter(cli).statusOf(jobApp);
        assertEquals(expectedStatus, actualStatus);
    }
}