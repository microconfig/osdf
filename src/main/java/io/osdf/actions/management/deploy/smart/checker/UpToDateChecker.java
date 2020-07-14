package io.osdf.actions.management.deploy.smart.checker;

import io.osdf.core.application.core.Application;

public interface UpToDateChecker {
    boolean check(Application app);
}
