package io.microconfig.osdf.api;

import io.microconfig.osdf.api.declarations.*;
import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.api.ApiCallFinder.finder;
import static io.microconfig.osdf.api.MainApiReader.apiInfo;
import static io.microconfig.osdf.api.implementations.ChaosApiImpl.chaosApi;
import static io.microconfig.osdf.api.implementations.ComponentsApiImpl.componentsApi;
import static io.microconfig.osdf.api.implementations.FrequentlyUsedApiImpl.frequentlyUsedApi;
import static io.microconfig.osdf.api.implementations.InfoApiImpl.infoApi;
import static io.microconfig.osdf.api.implementations.InitializationApiImpl.initializationApi;
import static io.microconfig.osdf.api.implementations.InstallApiImpl.installApi;
import static io.microconfig.osdf.api.implementations.LoadTestingApiImpl.loadTestingApi;
import static io.microconfig.osdf.api.implementations.ManagementApiImpl.managementApi;
import static io.microconfig.osdf.api.implementations.SystemApiImpl.systemApi;

@RequiredArgsConstructor
public class MainApiCaller implements ApiCaller {
    private final OSDFPaths paths;
    private final ClusterCLI cli;

    public static ApiCaller mainApi(OSDFPaths paths, ClusterCLI cli) {
        return new MainApiCaller(paths, cli);
    }

    @Override
    public void call(List<String> args) {
        if (args.isEmpty()) {
            apiInfo(MainApi.class).printHelp();
            return;
        }
        ApiCallerImpl.builder()
                .finder(finder(MainApi.class))
                .addImpl(FrequentlyUsedApi.class, frequentlyUsedApi(paths))
                .addImpl(InstallApi.class, installApi(paths))
                .addImpl(InitializationApi.class, initializationApi(paths))
                .addImpl(ComponentsApi.class, componentsApi(paths))
                .addImpl(ManagementApi.class, managementApi(paths, cli))
                .addImpl(InfoApi.class, infoApi(paths, cli))
                .addImpl(SystemApi.class, systemApi(paths))
                .addImpl(ChaosApi.class, chaosApi(paths, cli))
                .addImpl(LoadTestingApi.class, loadTestingApi(paths, cli))
                .build()
                .call(args);
    }
}
