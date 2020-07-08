package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.healthcheck.PodsInfo;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.actions.info.status.service.ServiceStatus;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.List;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.osdf.actions.info.healthcheck.PodsInfo.podsInfo;
import static io.osdf.actions.info.status.service.ServiceStatus.NOT_FOUND;
import static io.osdf.actions.info.status.service.ServiceStatus.READY;
import static io.osdf.actions.info.status.service.ServiceStatusGetter.serviceStatusGetter;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;
import static java.util.stream.IntStream.range;


public class DeploymentStatusRows implements RowColumnsWithStatus {
    private final ClusterCli cli;
    private final ServiceApplication service;

    private final ColumnPrinter printer;
    private final boolean withHealthCheck;
    private final boolean status;

    public DeploymentStatusRows(ClusterCli cli, ServiceApplication service,
                                ColumnPrinter printer, boolean withHealthCheck) {
        this.cli = cli;
        this.service = service;
        this.printer = printer;
        this.withHealthCheck = withHealthCheck;
        this.status = fetch();
    }

    public static DeploymentStatusRows deploymentStatusRows(ClusterCli cli, ServiceApplication service,
                                                            ColumnPrinter printer, boolean withHealthCheck) {
        return new DeploymentStatusRows(cli, service, printer, withHealthCheck);
    }

    @Override
    public List<String> getColumns() {
        return printer.getColumns();
    }

    @Override
    public List<List<String>> getRows() {
        return printer.getRows();
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    private boolean fetch() {
        if (!service.exists()) {
            addNotFoundRow();
            return false;
        }

        ClusterDeployment deployment = service.deployment();
        ServiceStatus status = serviceStatusGetter(cli).statusOf(service);
        if (withHealthCheck) {
            PodsInfo podsInfo = podsInfo(deployment, service.files());
            addMainRow(deployment, status);
            addPods(podsInfo.getPods(), podsInfo.getPodsHealth());
            return podsInfo.isHealthy() && status == READY;
        }
        addMainRow(deployment, status);
        return status == READY;
    }

    private void addMainRow(ClusterDeployment deployment, ServiceStatus status) {
        YamlObject yaml = yaml(service.files().getPath("deploy.yaml"));
        printer.addRow(green(service.files().name()),
                green(formatVersions(service.coreDescription().getAppVersion(), yaml.get("app.version"))),
                green(formatVersions(service.coreDescription().getConfigVersion(), yaml.get("config.version"))),
                coloredStatus(status),
                green(replicas(deployment)));
    }

    private void addNotFoundRow() {
        YamlObject yaml = yaml(service.files().getPath("deploy.yaml"));
        printer.addRow(green(service.files().name()),
                green(formatVersions("-", yaml.get("app.version"))),
                green(formatVersions("-", yaml.get("config.version"))),
                coloredStatus(NOT_FOUND),
                green("-"));
    }

    private String formatVersions(String remote, String local) {
        if (remote.equalsIgnoreCase(local)) return remote;
        return remote + " [" + local + "]";
    }

    private String coloredStatus(ServiceStatus status) {
        String statusString = status.toString().replace("_", " ");
        switch (status) {
            case READY:
                return green(statusString);
            case FAILED:
            case NOT_READY:
                return red(statusString);
            case RUNNING:
            default:
                return statusString;
        }
    }

    private void addPods(List<Pod> pods, List<Boolean> podsHealth) {
        range(0, pods.size()).forEach(i -> addPodRow(pods.get(i), podsHealth.get(i)));
    }

    private void addPodRow(Pod pod, boolean health) {
        printer.addRow(" - " + pod.getName(), "", "", health ? green("OK") : red("BAD"), "");
    }

    private String replicas(ClusterDeployment deployment) {
        ClusterResource resource = deployment.toResource();
        if (!resource.exists(cli)) return "-";
        ResourceProperties properties = resourceProperties(cli, resource, of(
                "available", "status.availableReplicas",
                "replicas", "spec.replicas"
        ));
        return properties.get("available") + "/" + properties.get("replicas");
    }
}