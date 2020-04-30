package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.ComponentsApi;
import io.microconfig.osdf.commands.PropertiesDiffCommand;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ComponentsApiImpl implements ComponentsApi {
    private final OSDFPaths paths;

    public static ComponentsApi componentsApi(OSDFPaths paths) {
        return new ComponentsApiImpl(paths);
    }

    @Override
    public void propertiesDiff(List<String> components) {
        new PropertiesDiffCommand(paths).show(components);
    }

    @Override
    public void changeVersion(List<String> components) {
        throw new OSDFException("Not Implemented yet");
    }
}
