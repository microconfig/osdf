package io.microconfig.osdf.deprecated.components;

import io.microconfig.osdf.cluster.openshift.OpenShiftCLI;

import java.nio.file.Path;

import static java.nio.file.Files.exists;
import static java.nio.file.Path.*;

public enum ComponentType {
    JOB {
        @Override
        public AbstractOpenShiftComponent component(String name, String version, Path configDir, OpenShiftCLI oc) {
            return new JobComponent(name, version, configDir, oc);
        }
    },
    DEPLOYMENT {
        @Override
        public AbstractOpenShiftComponent component(String name, String version, Path configDir, OpenShiftCLI oc) {
            return new DeploymentComponent(name, version, configDir, oc);
        }
    };

    public boolean checkDir(Path dir) {
        return exists(of(dir + "/" + toString().toLowerCase() + ".yaml"));
    }

    public abstract AbstractOpenShiftComponent component(String name, String version, Path configDir, OpenShiftCLI oc);
}
