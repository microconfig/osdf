package io.microconfig.osdf.api.v2.impls;

import io.microconfig.osdf.api.v2.apis.InfoApi;
import io.microconfig.osdf.commands.LogsCommand;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.OCExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InfoApiImpl implements InfoApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static InfoApi infoApi(OSDFPaths paths, OCExecutor oc) {
        return new InfoApiImpl(paths, oc);
    }

    @Override
    public void logs(String component, String pod) {
        new LogsCommand(paths, oc).show(component, pod);
    }

    @Override
    public void status(List<String> components, Boolean withHealthCheck) {
        throw new OSDFException("Not Implemented yet");
    }
}
