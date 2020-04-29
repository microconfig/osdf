package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.ManagementApi;
import io.microconfig.osdf.commands.DeletePodCommand;
import io.microconfig.osdf.commands.RestartCommand;
import io.microconfig.osdf.commands.StopCommand;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ManagementApiImpl implements ManagementApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static ManagementApi managementApi(OSDFPaths paths, OCExecutor oc) {
        return new ManagementApiImpl(paths, oc);
    }

    @Override
    public void deploy(List<String> components, String mode, Boolean wait) {
        throw new OSDFException("Not Implemented yet");
    }

    @Override
    public void restart(List<String> components) {
        new RestartCommand(paths, oc).run(components);
    }

    @Override
    public void stop(List<String> components) {
        new StopCommand(paths, oc).run(components);
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        new DeletePodCommand(paths, oc).delete(component, pods);
    }
}
