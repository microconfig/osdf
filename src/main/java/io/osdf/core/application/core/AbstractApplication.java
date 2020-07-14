package io.osdf.core.application.core;

import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.common.utils.YamlUtils.createFromString;
import static io.osdf.core.cluster.resource.ClusterResourceImpl.clusterResource;

@RequiredArgsConstructor
public class AbstractApplication implements Application {
    private final ClusterCli cli;
    private final ApplicationFiles files;

    private CoreDescription coreDescription = null;

    public static AbstractApplication application(ClusterCli cli, ApplicationFiles files) {
        return new AbstractApplication(cli, files);
    }

    @Override
    public String name() {
        return files.name();
    }

    @Override
    public boolean exists() {
        return cli.execute("get configmap " + descriptionConfigMapName()).ok();
    }

    @Override
    public ApplicationFiles files() {
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
    }

    @Override
    public CoreDescription coreDescription() {
        if (coreDescription != null) return coreDescription;
        return coreDescription = loadDescription(CoreDescription.class, "core");
    }

    public <T> T loadDescription(Class<T> descriptionClass, String key) {
        String content = cli.execute("get configmap " + descriptionConfigMapName() + " -o custom-columns=\"config:.data." + key + "\"")
                .throwExceptionIfError()
                .getOutput()
                .replaceFirst("config\n", "");
        return createFromString(descriptionClass, content);
    }

    public String descriptionConfigMapName() {
        return name() + "-osdf";
    }
}
