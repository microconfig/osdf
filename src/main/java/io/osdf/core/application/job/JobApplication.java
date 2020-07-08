package io.osdf.core.application.job;

import io.osdf.core.application.ApplicationManager;
import io.osdf.core.application.CoreDescription;
import io.osdf.core.application.ResourceDescription;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.cluster.job.ClusterJob;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.YamlUtils.createFromString;
import static io.osdf.core.application.ApplicationManager.applicationManager;
import static io.osdf.core.application.DescriptionUploader.descriptionUploader;
import static io.osdf.core.cluster.job.ClusterJobImpl.clusterJob;
import static java.util.Map.of;

@RequiredArgsConstructor
public class JobApplication {
    private final String name;
    private final ApplicationFiles files;
    private final ApplicationManager manager;
    private final ClusterCli cli;

    private CoreDescription coreDescription = null;
    private ClusterJob job = null;

    public static JobApplication jobApplication(ApplicationFiles files, ClusterCli cli) {
        return new JobApplication(files.name(), files, applicationManager(files.name(), cli), cli);
    }

    public void uploadDescription() {
        descriptionUploader(cli).upload(files.name() + "-osdf", of(
                "job", JobDescription.from(files),
                "core", CoreDescription.from(files)
        ));
    }

    public ClusterJob job() {
        if (job != null) return job;
        ResourceDescription description = createFromString(JobDescription.class, manager.applicationConfig("job")).getJob();
        job = clusterJob(description.getName(), cli);
        return job;
    }

    public CoreDescription coreDescription() {
        if (coreDescription != null) return coreDescription;
        coreDescription = createFromString(CoreDescription.class, manager.applicationConfig("core"));
        return coreDescription;
    }

    public ApplicationFiles files() {
        return files;
    }

    public String name() {
        return name;
    }

    public boolean exists() {
        return manager.applicationExists();
    }
}
