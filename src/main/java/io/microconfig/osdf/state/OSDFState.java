package io.microconfig.osdf.state;

import io.microconfig.osdf.nexus.NexusArtifact;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;

import static io.microconfig.osdf.state.ConfigSource.*;
import static io.microconfig.osdf.state.OSDFStateChecker.stateChecker;
import static io.microconfig.osdf.utils.YamlUtils.createFromFile;
import static java.nio.file.Files.newBufferedWriter;

@Getter
@Setter
public class OSDFState {
    private String gitUrl;
    private String nexusUrl;
    private NexusArtifact configsNexusArtifact;
    private String localConfigs;
    private ConfigSource configSource;

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
        try {
            new Yaml().dump(this, newBufferedWriter(path));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't dump state file", e);
        }
    }

    private void configureConfigSource() {
        if (configSource == null) {
            if (localConfigs != null) configSource = LOCAL;
            else if (gitUrl != null) configSource = GIT;
            else if (nexusUrl != null) configSource = NEXUS;
        }
    }
}
