package io.osdf.core.application.core;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.loaders.ApplicationMapper;
import io.osdf.core.application.job.JobApplicationMapper;
import io.osdf.core.application.service.ServiceApplicationMapper;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.application.job.JobApplicationMapper.job;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;

@RequiredArgsConstructor
public class AllApplications implements ApplicationMapper<Application> {
    private final ServiceApplicationMapper serviceMapper;
    private final JobApplicationMapper jobMapper;

    public static AllApplications all(ClusterCli cli) {
        return new AllApplications(service(cli), job(cli));
    }

    @Override
    public boolean check(ApplicationFiles files) {
        return serviceMapper.check(files) || jobMapper.check(files);
    }

    @Override
    public Application map(ApplicationFiles files) {
        if (serviceMapper.check(files)) return serviceMapper.map(files);
        if (jobMapper.check(files)) return jobMapper.map(files);

        throw new OSDFException("Unknown app format: " + files.name());
    }
}
