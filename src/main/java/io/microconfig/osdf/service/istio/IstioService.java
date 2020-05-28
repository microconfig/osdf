package io.microconfig.osdf.service.istio;

import io.microconfig.osdf.istio.VirtualService;
import io.microconfig.osdf.service.ClusterService;

public interface IstioService extends ClusterService {
    VirtualService virtualService();
}
