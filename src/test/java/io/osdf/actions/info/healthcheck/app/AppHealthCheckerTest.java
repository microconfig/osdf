package io.osdf.actions.info.healthcheck.app;

import io.osdf.core.application.core.Application;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class AppHealthCheckerTest {

    @Test
    void testOf() {
        assertClasses(ServiceApplication.class, ServiceHealthChecker.class);
        assertClasses(JobApplication.class, JobHealthChecker.class);
    }

    void assertClasses(Class<? extends Application> appClass, Class<?> checkerClass) {
        checkerClass.isAssignableFrom(AppHealthChecker.of(mock(appClass), null).getClass());
    }
}