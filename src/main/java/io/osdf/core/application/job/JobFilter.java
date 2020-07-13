package io.osdf.core.application.job;

import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.application.local.loaders.ApplicationMapper;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.application.job.JobApplication.jobApplication;

@RequiredArgsConstructor
public class JobFilter implements ApplicationMapper<JobApplication> {
    private final ClusterCli cli;

    public static JobFilter job(ClusterCli cli) {
        return new JobFilter(cli);
    }

    @Override
    public boolean check(ApplicationFiles files) {
        return files.metadata().getType().equals("JOB");
    }

    @Override
    public JobApplication map(ApplicationFiles files) {
        return jobApplication(files, cli);
    }
}
