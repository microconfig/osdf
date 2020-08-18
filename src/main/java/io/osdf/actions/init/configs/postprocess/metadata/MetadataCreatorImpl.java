package io.osdf.actions.init.configs.postprocess.metadata;

import io.osdf.actions.init.configs.postprocess.types.MetadataType;
import io.osdf.core.application.core.files.metadata.LocalResourceMetadata;
import io.osdf.core.local.component.ComponentDir;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.osdf.actions.init.configs.postprocess.metadata.resources.ResourceMetadataCollector.resourceMetadataCollector;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.PLAIN;
import static io.osdf.common.utils.YamlUtils.dump;
import static io.osdf.core.application.core.files.metadata.ApplicationMetadata.appMetadata;
import static java.nio.file.Path.of;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNullElse;

@RequiredArgsConstructor
public class MetadataCreatorImpl implements MetadataCreator {
    public static MetadataCreatorImpl metadataCreator() {
        return new MetadataCreatorImpl();
    }

    @Override
    public void create(ComponentDir componentDir) {
        List<LocalResourceMetadata> resourcesMetadata = resourceMetadataCollector()
                .collect(componentDir.getPath("resources"));

        MetadataType type = appType(resourcesMetadata);
        LocalResourceMetadata mainResource = resourcesMetadata.stream()
                .filter(type.condition())
                .findFirst()
                .orElse(null);
        dump(appMetadata(type.name(), resourcesMetadata, mainResource), of(componentDir.root() + "/osdf-metadata.yaml"));
    }

    private MetadataType appType(List<LocalResourceMetadata> resourcesMetadata) {
        MetadataType metadataType = stream(MetadataType.values())
                .filter(type -> resourcesMetadata.stream().anyMatch(type.condition()))
                .findFirst()
                .orElse(null);
        return requireNonNullElse(metadataType, PLAIN);
    }
}
