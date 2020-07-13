package io.osdf.core.application.service;

import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.application.local.loaders.ApplicationMapper;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.application.service.ServiceApplication.serviceApplication;

@RequiredArgsConstructor
public class ServiceApplicationMapper implements ApplicationMapper<ServiceApplication> {
    private final ClusterCli cli;

    public static ServiceApplicationMapper service(ClusterCli cli) {
        return new ServiceApplicationMapper(cli);
    }

    @Override
    public boolean check(ApplicationFiles files) {
        return files.metadata().getType().equals("SERVICE");
    }

    @Override
    public ServiceApplication map(ApplicationFiles files) {
        return serviceApplication(files, cli);
    }
}
