package io.osdf.actions.init.configs.postprocess;

import io.osdf.actions.init.configs.postprocess.metadata.MetadataCreator;
import io.osdf.actions.init.configs.postprocess.types.ComponentType;
import io.osdf.core.local.component.ComponentDir;

import static io.osdf.actions.init.configs.postprocess.ConfigMapCreator.configMapCreator;
import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static java.nio.file.Files.exists;
import static java.util.Arrays.stream;

public class ComponentPostProcessor {
    private final ConfigMapCreator configMapCreator = configMapCreator();
    private final MetadataCreator metadataCreator = metadataCreator();

    public static ComponentPostProcessor componentPostProcessor() {
        return new ComponentPostProcessor();
    }

    public void process(ComponentDir componentDir) {
        if (!exists(componentDir.getPath("resources"))) return;
        configMapCreator.createConfigMaps(componentDir);
        stream(ComponentType.values()).forEach(type -> metadataCreator.create(type, componentDir));
    }
}
