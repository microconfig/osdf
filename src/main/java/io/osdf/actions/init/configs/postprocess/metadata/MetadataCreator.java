package io.osdf.actions.init.configs.postprocess.metadata;

import io.osdf.core.local.component.ComponentDir;

public interface MetadataCreator {
    void create(ComponentDir componentDir);
}
