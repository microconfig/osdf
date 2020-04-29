package io.microconfig.osdf.state;

import io.microconfig.osdf.configs.ConfigsSource;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.openshift.OpenShiftCredentials;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

import static io.microconfig.osdf.configs.ConfigsSource.*;
import static io.microconfig.osdf.state.OSDFStateChecker.stateChecker;
import static io.microconfig.osdf.utils.YamlUtils.createFromFile;
import static io.microconfig.osdf.utils.YamlUtils.dump;

@Getter
@Setter
public class OSDFState {
    private String gitUrl;
    private String nexusUrl;
    private NexusArtifact configsNexusArtifact;
    private String localConfigs;
    private ConfigsSource configsSource;

    private OpenShiftCredentials openShiftCredentials;
    private Credentials nexusCredentials;

    private String env;
    private String configVersion;

    private String projectVersion;
    private String group;
    private List<String> components;

    private String osdfVersion;

    public static void createState(OSDFVersion version, Path stateSavePath) {
        OSDFState state = new OSDFState();
        state.setOsdfVersion(version.toString());
        state.save(stateSavePath);
    }

    public static OSDFState fromFile(Path stateSavePath) {
        return createFromFile(OSDFState.class, stateSavePath);
    }

    public boolean check() {
        OSDFStateChecker checker = stateChecker(this);
        return checker.checkConfigSource() && checker.checkOpenShiftCredentials() && checker.checkEnv();
    }

    public <T> void setIfNotNull(BiConsumer<OSDFState, ? super T> setter, T value) {
        if (value != null) setter.accept(this, value);
    }

    public void save(Path path) {
        configureConfigSource();
        dump(this, path);
    }

    private void configureConfigSource() {
        if (configsSource == null) {
            if (localConfigs != null) configsSource = LOCAL;
            else if (gitUrl != null) configsSource = GIT;
            else if (nexusUrl != null) configsSource = NEXUS;
        }
    }
}
