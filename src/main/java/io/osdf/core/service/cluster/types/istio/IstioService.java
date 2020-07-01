package io.osdf.core.service.cluster.types.istio;

import io.osdf.common.exceptions.OSDFException;
import unstable.io.osdf.istio.VirtualService;
import io.osdf.core.service.cluster.ClusterService;

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
