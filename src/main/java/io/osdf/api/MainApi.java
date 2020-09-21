package io.osdf.api;

import io.osdf.actions.configs.ConfigsApi;
import io.osdf.actions.info.api.InfoApi;
import io.osdf.actions.init.InitializationApi;
import io.osdf.actions.management.ManagementApi;
import io.osdf.actions.system.SystemApi;
import io.osdf.api.lib.annotations.ApiGroup;
import io.osdf.api.lib.annotations.Named;
import io.osdf.api.lib.annotations.Public;

@Public({"management", "info", "configs", "system"})
public interface MainApi {
    @ApiGroup(ManagementApi.class)
    void management();

    @ApiGroup(InfoApi.class)
    void info();

    @ApiGroup(ConfigsApi.class)
    void configs();

    @Named
    @ApiGroup(InitializationApi.class)
    void init();

    @ApiGroup(SystemApi.class)
    void system();
}
