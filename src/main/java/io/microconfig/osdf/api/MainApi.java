package io.microconfig.osdf.api;

import io.microconfig.osdf.api.annotation.Import;
import io.microconfig.osdf.api.annotation.Named;
import io.microconfig.osdf.api.declarations.*;

public interface MainApi {
    @Import(api = InstallApi.class, order = 1)
    void install();

    @Named
    @Import(api = InitializationApi.class, order = 2)
    void init();

    @Import(api = ManagementApi.class, order = 3)
    void management();

    @Import(api = InfoApi.class, order = 4)
    void info();

    @Import(api = FrequentlyUsedApi.class, order = 5)
    void frequent();

    @Import(api = ComponentsApi.class, order = 6)
    void components();

    @Import(api = SystemApi.class, order = 7)
    void system();

    @Named(as = "new")
    @Import(api = NewManagementApi.class, order = 8)
    void newManagement();
}
