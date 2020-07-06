package io.osdf.actions.init.configs.postprocess.metadata;

import io.osdf.actions.init.configs.postprocess.types.ComponentType;
import io.osdf.core.local.component.ComponentDir;
import io.osdf.core.service.metadata.LocalResourceMetadata;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.init.configs.postprocess.metadata.resources.ResourceMetadataCollector.resourceMetadataCollector;
import static io.osdf.common.utils.YamlUtils.dump;
import static io.osdf.core.service.metadata.ComponentMetadata.componentMetadata;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class MetadataCreatorImpl implements MetadataCreator {
    public static MetadataCreatorImpl metadataCreator() {
        return new MetadataCreatorImpl();
    }

    @Override
    public void create(ComponentType type, ComponentDir componentDir) {
        List<LocalResourceMetadata> resourcesMetadata = resourceMetadataCollector()
                .collect(componentDir.getPath("resources"));
        boolean isService = resourcesMetadata.stream()
                .anyMatch(type.condition());
        if (!isService) return;
        dump(componentMetadata(type.name(), resourcesMetadata), of(componentDir.root() + "/osdf-metadata.yaml"));
    }
}
