package io.osdf.actions.management.deploy.deployer;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.Logger.info;

@RequiredArgsConstructor
public class ImmutableAwareUploader {
    private final ClusterCli cli;

    public static ImmutableAwareUploader immutableAwareUploader(ClusterCli cli) {
        return new ImmutableAwareUploader(cli);
    }

    public void uploadResources(ApplicationFiles files) {
        CliOutput output = cli.execute("apply -f " + files.getPath("resources"));
        if (!output.ok()) {
            if (output.getOutput().contains("field is immutable")) {
                info("One of resources changed immutable field");
                files.resources().forEach(resource -> resource.upload(cli));
            } else {
                throw new OSDFException("Error deploying " + files.name() + ":" + output.getOutput());
            }
        }
    }
}
