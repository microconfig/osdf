package io.osdf.core.service.core.deployment.pack.loader;

import io.osdf.core.service.core.deployment.pack.ServiceDeployPack;

import java.util.List;

public interface ServiceDeployPacksLoader {
    List<ServiceDeployPack> loadPacks();

    ServiceDeployPack loadByName(String name);
}
