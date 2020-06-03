package io.microconfig.osdf.utils;

import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.service.ClusterService;
import io.microconfig.osdf.service.istio.IstioService;

public class IstioUtils {
    private IstioUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static IstioService toIstioService(ClusterService service) {
        if (isIstioService(service)) return (IstioService) service;
        throw new OSDFException(service.name() + " is not istio service.");
    }

    public static boolean isIstioService(ClusterService service) {
        return service instanceof IstioService;
    }
}
