package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.LoadTestingApi;
import io.microconfig.osdf.commands.LoadTestCommand;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import static io.microconfig.osdf.deployers.TemplateDeployer.templateDeployer;

@RequiredArgsConstructor
public class LoadTestingApiImpl implements LoadTestingApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static LoadTestingApi loadTestingApi(OSDFPaths paths, OCExecutor oc) {
        return new LoadTestingApiImpl(paths, oc);
    }

    @Override
    public void loadTest(Path jmeterPlanPath, Integer numberOfSlaves) {
        int number = numberOfSlaves != null ? numberOfSlaves : 3;
        new LoadTestCommand(paths, jmeterPlanPath, number, oc, templateDeployer()).run();
    }
}
