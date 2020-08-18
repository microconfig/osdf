package io.osdf.actions.info.healthcheck.app;

import io.osdf.core.application.core.Application;

public class PlainAppHealthChecker implements AppHealthChecker {
    public static PlainAppHealthChecker plainAppHealthChecker() {
        return new PlainAppHealthChecker();
    }

    @Override
    public boolean check(Application application) {
        return application.exists();
    }
}
