package io.microconfig.osdf.service.istio;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.service.ClusterService;

public interface IstioService extends ClusterService {
    static IstioService toIstioService(ClusterService service) {
        if (isIstioService(service)) return (IstioService) service;
        throw new OSDFException(service.name() + " is not istio service.");
    }

    static boolean isIstioService(ClusterService service) {
        return service instanceof IstioService;
    }

    VirtualService virtualService();
}
