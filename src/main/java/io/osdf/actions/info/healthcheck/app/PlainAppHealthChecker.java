package io.osdf.actions.info.healthcheck.app;

import io.osdf.actions.management.deploy.deployer.AppHealth;
import io.osdf.core.application.core.Application;

import static io.osdf.actions.management.deploy.deployer.AppHealth.ERROR;
import static io.osdf.actions.management.deploy.deployer.AppHealth.OK;

public class PlainAppHealthChecker implements AppHealthChecker {
    public static PlainAppHealthChecker plainAppHealthChecker() {
        return new PlainAppHealthChecker();
    }

    @Override
    public AppHealth check(Application application) {
        return application.exists() ? OK : ERROR;
    }
}
