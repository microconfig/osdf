package io.microconfig.osdf.api.v2;

import io.microconfig.osdf.api.v2.apis.*;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.api.v2.ApiCallFinder.finder;
import static io.microconfig.osdf.api.v2.impls.ComponentsApiImpl.componentsApi;
import static io.microconfig.osdf.api.v2.impls.FrequentlyUsedApiImpl.frequentlyUsedApi;
import static io.microconfig.osdf.api.v2.impls.InfoApiImpl.infoApi;
import static io.microconfig.osdf.api.v2.impls.InitializationApiImpl.initializationApi;
import static io.microconfig.osdf.api.v2.impls.InstallApiImpl.installApi;
import static io.microconfig.osdf.api.v2.impls.ManagementApiImpl.managementApi;
import static io.microconfig.osdf.api.v2.impls.SystemApiImpl.systemApi;

@RequiredArgsConstructor
public class MainApi implements NewApiCaller {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static NewApiCaller mainApi(OSDFPaths paths, OCExecutor oc) {
        return new MainApi(paths, oc);
    }

    @Override
    public void call(List<String> args) {
        ApiCallerImpl.builder()
                .finder(finder(Api.class))
                .addImpl(FrequentlyUsedApi.class, frequentlyUsedApi(paths))
                .addImpl(InstallApi.class, installApi(paths))
                .addImpl(InitializationApi.class, initializationApi(paths))
                .addImpl(ComponentsApi.class, componentsApi(paths))
                .addImpl(ManagementApi.class, managementApi(paths, oc))
                .addImpl(InfoApi.class, infoApi(paths, oc))
                .addImpl(SystemApi.class, systemApi(paths))
                .build()
                .call(args);
    }
}
