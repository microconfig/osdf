package unstable.io.osdf.loadtesting.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static io.microconfig.utils.Logger.announce;
import static java.nio.file.Path.of;

@Getter
@RequiredArgsConstructor
public class JmeterConfigProcessor {
    private final JmeterMasterConfig masterConfig;
    private final List<JmeterSlaveConfig> slaveConfigs;
    private final Path jmeterComponentsPath;

    public static JmeterConfigProcessor configProcessor(Path jmeterComponentsPath, int numberOfSlaves, Path jmeterPlanPath) {
        JmeterMasterConfig masterConfig = JmeterMasterConfig.jmeterMasterConfig(jmeterComponentsPath, jmeterPlanPath);
        List<JmeterSlaveConfig> slaveConfigs = IntStream.range(0, numberOfSlaves)
                .map(i -> i + 1)
                .mapToObj(i -> "jmeter-slave-" + i)
                .map(name -> JmeterSlaveConfig.jmeterSlaveConfig(name, jmeterComponentsPath))
                .collect(Collectors.toList());
        return new JmeterConfigProcessor(masterConfig, slaveConfigs, jmeterComponentsPath);
    }

    public void init() {
        announce("Init configs");
        masterConfig.init();
        slaveConfigs.forEach(JmeterSlaveConfig::init);
    }

    public Map<String, Object> loadUserConfig() {
        return loadFromPath(of(jmeterComponentsPath + "/application.yaml"));
    }
}
