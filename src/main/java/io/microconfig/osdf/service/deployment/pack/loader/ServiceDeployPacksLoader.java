package io.microconfig.osdf.service.deployment.pack.loader;

import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;

import java.util.List;

public interface ServiceDeployPacksLoader {
    List<ServiceDeployPack> loadPacks();

    ServiceDeployPack loadByName(String name);
}
