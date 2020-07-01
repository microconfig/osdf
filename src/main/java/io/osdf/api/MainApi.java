package io.osdf.api;

import io.osdf.actions.configs.ConfigsApi;
import io.osdf.actions.info.api.InfoApi;
import io.osdf.actions.init.api.InitializationApi;
import io.osdf.actions.management.ManagementApi;
import io.osdf.actions.system.SystemApi;
import io.osdf.api.lib.annotations.Import;
import io.osdf.api.lib.annotations.Named;

public interface MainApi {
    @Import(api = ManagementApi.class, order = 1)
    void management();

    @Import(api = InfoApi.class, order = 2)
    void info();

    @Import(api = ConfigsApi.class, order = 3)
    void configs();

    @Named
    @Import(api = InitializationApi.class, order = 4)
    void init();

    @Import(api = SystemApi.class, order = 5)
    void system();
}
