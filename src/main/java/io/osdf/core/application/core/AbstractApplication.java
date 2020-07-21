package io.osdf.core.application.core;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.info;
import static io.osdf.core.cluster.configmap.ConfigMapLoader.configMapLoader;
import static io.osdf.core.cluster.resource.ClusterResourceImpl.clusterResource;

@RequiredArgsConstructor
public class AbstractApplication implements Application {
    private final String name;
    private final ClusterCli cli;
    private final ApplicationFiles files;

    private CoreDescription coreDescription = null;

    public static AbstractApplication application(ClusterCli cli, ApplicationFiles files) {
        return new AbstractApplication(files.name(), cli, files);
    }

    public static AbstractApplication remoteApplication(String name, ClusterCli cli) {
        return new AbstractApplication(name, cli, null);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean exists() {
        return cli.execute("get configmap " + descriptionConfigMapName()).ok();
    }

    @Override
    public ApplicationFiles files() {
        if (files == null) throw new OSDFException("Application " + name + " is remote");
        return files;
    }

    @Override
    public void delete() {
        if (!exists()) return;
        coreDescription()
                .getResources().stream()
                .map(ClusterResourceImpl::fromOpenShiftNotation)
                .forEach(resource -> resource.delete(cli));
        clusterResource("configmap", descriptionConfigMapName()).delete(cli);
        info("Deleted " + name());
    }

    @Override
    public CoreDescription coreDescription() {
        if (coreDescription != null) return coreDescription;
        coreDescription = loadDescription(CoreDescription.class, "core");
        return coreDescription;
    }

    public <T> T loadDescription(Class<T> descriptionClass, String key) {
        return configMapLoader(cli).load(descriptionConfigMapName(), key, descriptionClass);
    }

    public String descriptionConfigMapName() {
        return name() + "-osdf";
    }
}
