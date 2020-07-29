package io.osdf.core.application.job;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.AbstractApplication;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.description.ResourceDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.job.ClusterJob;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static io.osdf.core.application.core.AbstractApplication.application;
import static io.osdf.core.cluster.configmap.ConfigMapLoader.configMapLoader;
import static io.osdf.core.cluster.job.ClusterJobImpl.clusterJob;
import static java.util.Map.of;
import static java.util.Optional.empty;

@RequiredArgsConstructor
public class JobApplication implements Application {
    private final ClusterCli cli;
    private final AbstractApplication app;

    private ClusterJob job = null;

    public static JobApplication jobApp(ApplicationFiles files, ClusterCli cli) {
        return new JobApplication(cli, application(cli, files));
    }

    public static JobApplication jobApp(Application app) {
        if (!(app instanceof JobApplication)) throw new OSDFException(app.name() + "is not a job");
        return (JobApplication) app;
    }

    public void uploadDescription() {
        configMapLoader(cli).upload(app.descriptionConfigMapName(), of(
                "job", JobDescription.from(app.files()),
                "core", CoreDescription.from(app.files())
        ));
    }

    public Optional<ClusterJob> job() {
        if (job != null) return Optional.of(job);

        Optional<JobDescription> jobDescription = app.loadDescription(JobDescription.class, "job");
        if (jobDescription.isEmpty()) return empty();

        ResourceDescription description = jobDescription.get().getJob();
        job = clusterJob(description.getName(), cli);
        return Optional.of(job);
    }

    public ClusterJob getJobOrThrow() {
        return job().orElseThrow(() -> new OSDFException(name() + " not found"));
    }

    @Override
    public String name() {
        return app.name();
    }

    @Override
    public boolean exists() {
        return app.exists();
    }

    @Override
    public void delete() {
       app.delete();
    }

    @Override
    public ApplicationFiles files() {
        return app.files();
    }

    @Override
    public Optional<CoreDescription> coreDescription() {
        return app.coreDescription();
    }
}
