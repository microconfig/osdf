package io.osdf.actions.init.configs.postprocess.metadata;

import io.osdf.actions.init.configs.postprocess.types.MetadataType;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.application.core.files.metadata.LocalResourceMetadata;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.init.configs.postprocess.metadata.resources.ResourceMetadataCollector.resourceMetadataCollector;
import static io.osdf.common.utils.YamlUtils.dump;
import static io.osdf.core.application.core.files.metadata.ApplicationMetadata.serviceMetadata;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class MetadataCreatorImpl implements MetadataCreator {
    public static MetadataCreatorImpl metadataCreator() {
        return new MetadataCreatorImpl();
    }

    @Override
    public void create(MetadataType type, ComponentDir componentDir) {
        List<LocalResourceMetadata> resourcesMetadata = resourceMetadataCollector()
                .collect(componentDir.getPath("resources"));
        LocalResourceMetadata mainResource = resourcesMetadata.stream()
                .filter(type.condition())
                .findFirst()
                .orElse(null);
        if (mainResource == null) return;
        dump(serviceMetadata(type.name(), resourcesMetadata, mainResource), of(componentDir.root() + "/osdf-metadata.yaml"));
    }
}
