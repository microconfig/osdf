package io.osdf.core.application.core;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.loaders.ApplicationMapper;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.core.application.job.JobApplicationMapper.job;
import static io.osdf.core.application.plain.PlainApplicationMapper.plain;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static java.util.List.of;

@RequiredArgsConstructor
public class AllApplications implements ApplicationMapper<Application> {
    private final List<ApplicationMapper<? extends Application>> mappers;

    public static AllApplications all(ClusterCli cli) {
        return new AllApplications(of(service(cli), job(cli), plain(cli)));
    }

    @Override
    public boolean check(ApplicationFiles files) {
        return mappers.stream().anyMatch(mapper -> mapper.check(files));
    }

    @Override
    public Application map(ApplicationFiles files) {
        return mappers.stream()
                .filter(mapper -> mapper.check(files))
                .map(mapper -> mapper.map(files))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Unknown app format: " + files.name()));
    }
}
