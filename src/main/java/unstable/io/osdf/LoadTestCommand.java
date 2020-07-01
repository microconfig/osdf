package unstable.io.osdf;

import io.osdf.core.connection.cli.ClusterCli;
import unstable.io.osdf.loadtesting.JmeterComponent;
import unstable.io.osdf.loadtesting.JmeterResourcesCleaner;
import unstable.io.osdf.loadtesting.configs.JmeterConfigProcessor;
import unstable.io.osdf.loadtesting.loader.JmeterComponentsLoader;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static unstable.io.osdf.loadtesting.JmeterDeployUtils.*;
import static io.microconfig.utils.Logger.announce;

@RequiredArgsConstructor
public class LoadTestCommand {
    private final ClusterCli cli;
    private final JmeterConfigProcessor jmeterConfigProcessor;

    public static LoadTestCommand loadTestCommand(ClusterCli cli, JmeterConfigProcessor jmeterConfigProcessor) {
        return new LoadTestCommand(cli, jmeterConfigProcessor);
    }

    public void run() {
        announce("Load components");
        JmeterComponentsLoader jmeterLoader = JmeterComponentsLoader.jmeterComponentsLoader(cli, jmeterConfigProcessor);
        List<JmeterComponent> slaveComponents = jmeterLoader.loadSlaves();
        JmeterComponent masterComponent = jmeterLoader.loadMaster();

        try (JmeterResourcesCleaner ignore = JmeterResourcesCleaner.jmeterResourcesCleaner(jmeterLoader)) {
            deployDeployments(slaveComponents);
            setSlavesHosts(jmeterConfigProcessor, slaveComponents);
            deployDeployments(masterComponent);
            announce(waitResults(jmeterConfigProcessor, masterComponent));
        }
    }
}
