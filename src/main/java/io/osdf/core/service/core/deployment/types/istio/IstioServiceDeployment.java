package io.osdf.core.service.core.deployment.types.istio;

import io.osdf.core.service.core.deployment.ServiceDeployment;

public interface IstioServiceDeployment extends ServiceDeployment {
    String encodedVersion();
}
