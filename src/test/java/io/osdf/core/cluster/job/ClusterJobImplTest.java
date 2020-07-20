package io.osdf.core.cluster.job;

import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.api.JobApi;
import org.junit.jupiter.api.Test;

import static io.osdf.core.cluster.job.ClusterJobImpl.clusterJob;
import static io.osdf.test.cluster.api.JobApi.jobApi;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ClusterJobImplTest {
    @Test
    void testNameIsCorrect() {
        JobApi jobApi = jobApi("test");

        ClusterJobImpl job = clusterJob("test", jobApi);

        assertEquals("test", job.name());
    }

    @Test
    void testExistsAndDelete() {
        JobApi jobApi = jobApi("test");

        ClusterJobImpl job = clusterJob("test", jobApi);

        assertTrue(job.exists());
        job.delete();
        assertFalse(job.exists());
    }

    @Test
    void testToResource() {
        ClusterJobImpl job = clusterJob("test", mock(ClusterCli.class));
        ClusterResource resource = job.toResource();

        assertEquals("test", resource.name());
        assertEquals("job", resource.kind());
    }
}