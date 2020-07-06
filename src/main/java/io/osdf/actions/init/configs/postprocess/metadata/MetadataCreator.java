package io.osdf.actions.init.configs.postprocess.metadata;

import io.osdf.actions.init.configs.postprocess.types.ComponentType;
import io.osdf.core.local.component.ComponentDir;

public interface MetadataCreator {
    void create(ComponentType type, ComponentDir componentDir);
}
