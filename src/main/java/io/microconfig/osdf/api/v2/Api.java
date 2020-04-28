package io.microconfig.osdf.api.v2;

import io.microconfig.osdf.api.annotation.Import;
import io.microconfig.osdf.api.annotation.Named;
import io.microconfig.osdf.api.v2.apis.*;

public interface Api {
    @Import(InstallApi.class)
    void install();

    @Named
    @Import(InitializationApi.class)
    void init();
    
    @Import(ComponentsApi.class)
    void components();
    
    @Import(ManagementApi.class)
    void management();
    
    @Import(InfoApi.class)
    void info();
    
    @Import(SystemApi.class)
    void system();
}
