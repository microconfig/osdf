package io.microconfig.osdf.api.declarations;

import io.microconfig.osdf.api.annotation.ApiCommand;
import io.microconfig.osdf.api.annotation.ConsoleParam;
import io.microconfig.osdf.api.parameter.ComponentsParameter;

import java.util.List;

public interface ComponentsApi {
    @ApiCommand(description = "Show properties difference", order = 1)
    void propertiesDiff(@ConsoleParam(ComponentsParameter.class) List<String> components);

    @ApiCommand(description = "Change version of components", order = 2)
    void changeVersion(@ConsoleParam(ComponentsParameter.class) List<String> components);
}
