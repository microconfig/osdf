package io.microconfig.osdf.develop.service.deployment.pack.loader;

import io.microconfig.osdf.develop.service.deployment.pack.ServiceDeployPack;

import java.util.List;

public interface ServiceDeployPacksLoader {
    List<ServiceDeployPack> loadPacks();

    ServiceDeployPack loadByName(String name);
}
