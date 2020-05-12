package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;

import java.nio.file.Path;

import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;

public enum ComponentType {
    JOB {
        @Override
        public AbstractOpenShiftComponent component(String name, String version, Path configDir, OCExecutor oc) {
            return new JobComponent(name, version, configDir, oc);
        }
    },
    DEPLOYMENT {
        @Override
        public AbstractOpenShiftComponent component(String name, String version, Path configDir, OCExecutor oc) {
            return new DeploymentComponent(name, version, configDir, oc);
        }
    },
    TEMPLATE {
        @Override
        public AbstractOpenShiftComponent component(String name, String version, Path configDir, OCExecutor oc) {
            return new TemplateComponent(name, version, configDir, oc);
        }
    };

    public boolean checkDir(Path dir) {
        return exists(of(dir + "/" + toString().toLowerCase() + ".yaml"));
    }

    public abstract AbstractOpenShiftComponent component(String name, String version, Path configDir, OCExecutor oc);
}
