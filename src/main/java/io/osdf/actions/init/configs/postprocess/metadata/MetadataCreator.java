package io.osdf.actions.init.configs.postprocess.metadata;

import io.osdf.actions.init.configs.postprocess.types.MetadataType;
import io.osdf.core.local.component.ComponentDir;

public interface MetadataCreator {
    void create(MetadataType type, ComponentDir componentDir);
}
