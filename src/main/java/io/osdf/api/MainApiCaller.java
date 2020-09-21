package io.osdf.api;

import io.osdf.actions.configs.ConfigsApi;
import io.osdf.actions.info.api.InfoApi;
import io.osdf.actions.init.InitializationApi;
import io.osdf.actions.management.ManagementApi;
import io.osdf.actions.system.SystemApi;
import io.osdf.api.lib.ApiException;
import io.osdf.api.lib.apicall.ApiCaller;
import io.osdf.api.lib.apicall.ApiCallerImpl;
import io.osdf.api.lib.definitionparsers.ApiEntrypointDefinitionParserImpl;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.configs.ConfigsApiImpl.configsApi;
import static io.osdf.actions.info.api.InfoApiImpl.infoApi;
import static io.osdf.actions.init.InitializationApiImpl.initializationApi;
import static io.osdf.actions.management.ManagementApiImpl.managementApi;
import static io.osdf.actions.system.SystemApiImpl.systemApi;

@RequiredArgsConstructor
public class MainApiCaller implements ApiCaller {
    private final OsdfPaths paths;
    private final ClusterCli cli;

    public static ApiCaller mainApi(OsdfPaths paths, ClusterCli cli) {
        return new MainApiCaller(paths, cli);
    }

    @Override
    public void call(Class<?> apiEntrypointClass, List<String> args) {
        if (args.isEmpty()) {
            new ApiEntrypointDefinitionParserImpl().parse(apiEntrypointClass).printUsage();
            return;
        }
        try {
            ApiCallerImpl.builder()
                    .addImpl(InitializationApi.class, initializationApi(paths, cli))
                    .addImpl(ConfigsApi.class, configsApi(paths, cli))
                    .addImpl(ManagementApi.class, managementApi(paths, cli))
                    .addImpl(InfoApi.class, infoApi(paths, cli))
                    .addImpl(SystemApi.class, systemApi(paths))
                    .build().call(apiEntrypointClass, args);
        } catch (ApiException e) {
            throw new OSDFException(e.getMessage());
        }
    }
}
