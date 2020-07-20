package io.osdf.actions.init.configs.postprocess;

import io.osdf.actions.init.configs.postprocess.metadata.MetadataCreator;
import io.osdf.actions.init.configs.postprocess.types.MetadataType;
import io.osdf.core.local.component.ComponentDir;

import static io.osdf.actions.init.configs.postprocess.ConfigMapCreator.configMapCreator;
import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static java.util.Arrays.stream;

public class AppPostProcessor {
    private final ConfigMapCreator configMapCreator = configMapCreator();
    private final MetadataCreator metadataCreator = metadataCreator();

    public static AppPostProcessor componentPostProcessor() {
        return new AppPostProcessor();
    }

    public void process(ComponentDir componentDir) {
        configMapCreator.createConfigMaps(componentDir);
        stream(MetadataType.values()).forEach(type -> metadataCreator.create(type, componentDir));
    }
}
