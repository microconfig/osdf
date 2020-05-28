package io.microconfig.osdf.service.deployment.istio;

import io.microconfig.osdf.service.deployment.ServiceDeployment;

public interface IstioServiceDeployment extends ServiceDeployment {
    String encodedVersion();
}
