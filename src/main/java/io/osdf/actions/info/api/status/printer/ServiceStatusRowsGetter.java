package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.api.status.AppStatusRowsGetter;
import io.osdf.actions.info.healthcheck.PodsInfo;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.actions.info.status.service.ServiceStatus;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.cluster.deployment.ClusterDeployment;
import io.osdf.core.cluster.pod.Pod;
import io.osdf.core.cluster.resource.ClusterResource;
import io.osdf.core.cluster.resource.properties.ResourceProperties;
import io.osdf.core.connection.cli.ClusterCli;

import java.util.List;
import java.util.Optional;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static io.osdf.actions.info.api.status.printer.RowColumnsWithStatusImpl.rowColumnsWithStatus;
import static io.osdf.actions.info.api.status.printer.StatusRowsFormatter.formatter;
import static io.osdf.actions.info.healthcheck.PodsInfo.podsInfo;
import static io.osdf.actions.info.status.service.ServiceStatus.READY;
import static io.osdf.actions.info.status.service.ServiceStatusGetter.serviceStatusGetter;
import static io.osdf.core.cluster.resource.properties.ResourceProperties.resourceProperties;
import static java.util.Map.of;
import static java.util.stream.IntStream.range;

public class ServiceStatusRowsGetter implements AppStatusRowsGetter {
    private final ClusterCli cli;
    private final boolean withHealthCheck;

    public ServiceStatusRowsGetter(ClusterCli cli, boolean withHealthCheck) {
        this.cli = cli;
        this.withHealthCheck = withHealthCheck;
    }

    public static ServiceStatusRowsGetter serviceStatusRows(ClusterCli cli, boolean withHealthCheck) {
        return new ServiceStatusRowsGetter(cli, withHealthCheck);
    }

    @Override
    public RowColumnsWithStatus statusOf(Application app, ColumnPrinter printer) {
        ServiceApplication service = (ServiceApplication) app;
        return rowColumnsWithStatus(printer, addRowsToPrinterAndReturnStatus(service, formatter(printer)));
    }

    private boolean addRowsToPrinterAndReturnStatus(ServiceApplication service, StatusRowsFormatter formatter) {
        ServiceObjects serviceObjects = new ServiceObjects(service);
        if (!serviceObjects.exists) {
            formatter.addNotFoundRow(serviceObjects.files, coloredStatus(serviceObjects.serviceStatus));
            return false;
        }

        ServiceStatus serviceStatus = serviceStatusGetter(cli).statusOf(service);
        if (withHealthCheck) {
            return addMainRowWithPods(serviceObjects, formatter);
        }
        formatter.addMainRow(service.files(), serviceObjects.coreDescription, coloredStatus(serviceStatus), replicas(serviceObjects.deployment));
        return serviceStatus == READY;
    }

    private boolean addMainRowWithPods(ServiceObjects serviceObjects, StatusRowsFormatter formatter) {
        PodsInfo podsInfo = podsInfo(serviceObjects.deployment, serviceObjects.files);
        formatter.addMainRow(serviceObjects.files,
                serviceObjects.coreDescription,
                coloredStatus(serviceObjects.serviceStatus),
                replicas(serviceObjects.deployment));
        addPods(podsInfo.getPods(), podsInfo.getPodsHealth(), formatter.getPrinter());
        return podsInfo.isHealthy() && serviceObjects.serviceStatus == READY;
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

    private void addPods(List<Pod> pods, List<Boolean> podsHealth, ColumnPrinter printer) {
        range(0, pods.size()).forEach(i -> addPodRow(pods.get(i), podsHealth.get(i), printer));
    }

    private void addPodRow(Pod pod, boolean health, ColumnPrinter printer) {
        printer.addRow(" - " + pod.getName(), "", "", health ? green("OK") : red("BAD"), "");
    }

    private String replicas(ClusterDeployment deployment) {
        ClusterResource resource = deployment.toResource();
        Optional<ResourceProperties> propertiesOptional = resourceProperties(cli, resource, of(
                "available", "status.availableReplicas",
                "replicas", "spec.replicas"
        ));
        if (propertiesOptional.isEmpty()) return "?/?";

        ResourceProperties properties = propertiesOptional.get();
        return (properties.get("available").equals("<none>") ? "0" : properties.get("available")) + "/" + properties.get("replicas");
    }

    private class ServiceObjects {
        private CoreDescription coreDescription;
        private ClusterDeployment deployment;
        private ServiceStatus serviceStatus;
        private ApplicationFiles files;

        private final boolean exists;

        public ServiceObjects(ServiceApplication service) {
            Optional<CoreDescription> coreDescription = service.coreDescription();
            Optional<ClusterDeployment> deployment = service.deployment();
            if (deployment.isEmpty() || coreDescription.isEmpty()) {
                this.exists = false;
                return;
            }
            this.coreDescription = coreDescription.get();
            this.deployment = deployment.get();
            this.serviceStatus = serviceStatusGetter(cli).statusOf(service);
            this.files = service.files();
            this.exists = true;
        }
    }
}
