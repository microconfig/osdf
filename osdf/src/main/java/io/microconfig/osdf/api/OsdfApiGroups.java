package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.Group;
import io.microconfig.osdf.api.annotation.Named;
import io.microconfig.osdf.api.declarations.*;
import io.osdf.management.ManagementApi;

public interface OsdfApiGroups {
    @Group(api = InstallApi.class, order = 1)
    void install();

    @Named
    @Group(api = InitializationApi.class, order = 2)
    void init();

    @Group(api = ManagementApi.class, order = 3)
    void management();

    @Group(api = InfoApi.class, order = 4)
    void info();

    @Group(api = FrequentlyUsedApi.class, order = 5)
    void frequent();

    @Group(api = ComponentsApi.class, order = 6)
    void components();

    @Group(api = LoadTestingApi.class, order = 7)
    void loadTesting();

    @Group(api = SystemApi.class, order = 8)
    void system();

    @Group(api = ChaosApi.class, order = 8)
    void chaos();
}
