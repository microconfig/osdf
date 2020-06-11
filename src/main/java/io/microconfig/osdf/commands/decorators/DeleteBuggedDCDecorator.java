package io.microconfig.osdf.commands.decorators;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.commands.DeployCommand;
import io.microconfig.osdf.exceptions.StatusCodeException;
import io.microconfig.osdf.service.deployment.pack.ServiceDeployPack;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.microconfig.osdf.service.deployment.info.FailedRCsFinder.buggedDCsFinder;
import static io.microconfig.utils.Logger.info;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class DeleteBuggedDCDecorator {
    private final ClusterCLI cli;
    private final DeployCommand command;

    public static DeleteBuggedDCDecorator deleteFailed(ClusterCLI cli, DeployCommand command) {
        return new DeleteBuggedDCDecorator(cli, command);
    }

    public void deploy(List<String> serviceNames, String mode, boolean wait) {
        List<String> failed = firstDeploy(serviceNames, mode, wait)
                .stream()
                .map(pack -> pack.service().name())
                .collect(toUnmodifiableList());
        if (!wait) return;
        if (failed.isEmpty()) return;
        info("Failed services: " + failed);
        secondDeploy(failed, mode);
    }

    private List<ServiceDeployPack> firstDeploy(List<String> serviceNames, String mode, boolean wait) {
        deleteBuggedDCs();
        return command.deploy(serviceNames, mode, wait);
    }

    private void secondDeploy(List<String> failed, String mode) {
        List<String> redeployServices = getRedeployServices(failed);
        if (redeployServices.isEmpty()) return;

        List<ServiceDeployPack> finalFailedDeployments = command.deploy(redeployServices, mode, true);
        if (!finalFailedDeployments.isEmpty()) throw new StatusCodeException(1);
    }

    private List<String> getRedeployServices(List<String> failed) {
        List<String> buggedServiceNames = deleteBuggedDCs();
        Set<String> redeploySet = new HashSet<>();
        redeploySet.addAll(failed);
        redeploySet.addAll(buggedServiceNames);
        return new ArrayList<>(redeploySet);
    }

    private List<String> deleteBuggedDCs() {
        List<String> buggedDCNames = buggedDCsFinder(cli).find();
        List<String> buggedServiceNames = buggedDCNames.stream()
                .map(this::dcNameToServiceName)
                .collect(toUnmodifiableList());
        if (!buggedServiceNames.isEmpty()) {
            info("Services with bugged RC: " + buggedServiceNames);
        }
        buggedDCNames.forEach(name -> cli.execute("delete dc " + name));
        return buggedServiceNames;
    }

    private String dcNameToServiceName(String name) {
        return cli.execute("oc get dc " + name + " -o custom-columns=\"application:.metadata.labels.application\"")
                .throwExceptionIfError()
                .getOutputLines()
                .get(1)
                .strip();
    }
}
