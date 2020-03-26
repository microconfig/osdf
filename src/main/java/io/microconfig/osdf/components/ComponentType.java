package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;

import java.nio.file.Path;

import static java.nio.file.Files.exists;
import static java.nio.file.Path.*;

public enum ComponentType {
    JOB {
        @Override
        public AbstractOpenShiftComponent component(String name, Path configDir, Path openShiftConfigDir, OCExecutor oc) {
            return new JobComponent(name, configDir, openShiftConfigDir, oc);
        }
    },
    DEPLOYMENT {
        @Override
        public AbstractOpenShiftComponent component(String name, Path configDir, Path openShiftConfigDir, OCExecutor oc) {
            return new DeploymentComponent(name, configDir, openShiftConfigDir, oc);
        }
    };

    public boolean checkDir(Path dir) {
        return exists(of(dir + "/" + toString().toLowerCase() + ".yaml"));
    }

    public abstract AbstractOpenShiftComponent component(String name, Path configDir, Path openShiftConfigDir, OCExecutor oc);
}
