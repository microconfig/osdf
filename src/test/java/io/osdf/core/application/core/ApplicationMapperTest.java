package io.osdf.core.application.core;

import io.osdf.context.TestContext;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.osdf.context.TestContext.defaultContext;
import static io.osdf.core.application.core.AllApplications.all;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.appLoader;
import static io.osdf.core.application.job.JobApplicationMapper.job;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ApplicationMapperTest {
    private static TestContext context = defaultContext();

    @BeforeAll
    static void initConfigs() {
        context.initDev();
    }

    @Test
    void allApplications() {
        List<Application> apps = appLoader(context.getPaths()).load(all(mock(ClusterCli.class)));
        assertTrue(apps.stream().anyMatch(app -> app instanceof ServiceApplication));
        assertTrue(apps.stream().anyMatch(app -> app instanceof JobApplication));
    }

    @Test
    void jobsOnly() {
        List<JobApplication> apps = appLoader(context.getPaths()).load(job(mock(ClusterCli.class)));

        assertTrue(apps.stream().anyMatch(app -> app.name().equals("simple-job")));
    }

    @Test
    void servicesOnly() {
        List<ServiceApplication> apps = appLoader(context.getPaths()).load(service(mock(ClusterCli.class)));

        assertTrue(apps.stream().anyMatch(app -> app.name().equals("simple-service")));
    }
}