package io.osdf.actions.chaos.checks;

import io.osdf.actions.chaos.ChaosContext;
import io.osdf.actions.chaos.events.EventSender;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static io.osdf.actions.chaos.events.EventLevel.ERROR;
import static io.osdf.actions.chaos.events.EventLevel.INFO;
import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.core.application.core.files.loaders.ApplicationFilesLoaderImpl.activeRequiredAppsLoader;
import static io.osdf.core.application.service.ServiceApplicationMapper.service;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class LivenessChecker implements Checker {
    private final ClusterCli cli;
    private final List<ServiceApplication> apps;
    private final EventSender events;

    public static LivenessChecker livenessChecker(Object description, ChaosContext chaosContext) {
        List<ServiceApplication> apps = activeRequiredAppsLoader(chaosContext.paths(), null)
                .load(service(chaosContext.cli()));
        return new LivenessChecker(chaosContext.cli(), apps, chaosContext.eventStorage().sender("liveness checker"));
    }

    @Override
    public CheckerResponse check() {
        List<ServiceApplication> failed = apps.stream()
                .filter(not(this::hasReadyReplicas))
                .collect(toList());
        if (failed.isEmpty()) {
            events.send("All apps are healthy", INFO);
            return new CheckerResponse(true, "All apps are healthy");
        }

        List<String> appNames = appNames(failed);
        events.send("Failed apps - " + appNames, ERROR, appNames.toArray(String[]::new));
        return new CheckerResponse(false, "Failed apps - " + appNames);
    }

    private boolean hasReadyReplicas(ServiceApplication app) {
        return app.deployment().stream()
                .map(ClusterDeployment::toResource)
                .map(resource -> resourceProperties(
                        cli, resource,
                        of(
                                "ready", "status.readyReplicas",
                                "all", "status.replicas"
                        )
                )).filter(Optional::isPresent).map(Optional::get)
                .allMatch(this::readyReplicasExist);
    }

    private boolean readyReplicasExist(ResourceProperties resourceProperties) {
        Integer ready = castToInteger(resourceProperties.get("ready"));
        Integer all = castToInteger(resourceProperties.get("all"));
        if (all == null) return true;
        if (ready == null) return false;
        return all == 0 || ready > 0;
    }

    private List<String> appNames(List<ServiceApplication> failed) {
        return failed.stream().map(Application::name).collect(toUnmodifiableList());
    }
}
