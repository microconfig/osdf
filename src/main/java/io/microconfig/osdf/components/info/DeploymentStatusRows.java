package io.microconfig.osdf.components.info;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.printers.ColumnPrinter;

import java.util.List;

import static io.microconfig.osdf.components.info.DeploymentStatus.RUNNING;
import static io.microconfig.osdf.components.info.PodsHealthcheckInfo.podsInfo;
import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.ConsoleColor.red;
import static java.util.stream.IntStream.range;

public class DeploymentStatusRows implements RowColumnsWithStatus {
    private final DeploymentComponent component;
    private final ColumnPrinter printer;
    private final HealthChecker healthChecker;
    private final boolean status;

    public DeploymentStatusRows(DeploymentComponent component, ColumnPrinter printer, HealthChecker healthChecker) {
        this.component = component;
        this.printer = printer;
        this.healthChecker = healthChecker;
        this.status = fetch();
    }

    public static DeploymentStatusRows deploymentStatusRows(DeploymentComponent component, ColumnPrinter printer, HealthChecker healthChecker) {
        return new DeploymentStatusRows(component, printer, healthChecker);
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
        DeploymentInfo info = component.info();
        printer.addRow(green(component.getName()), green(component.getVersion()), coloredStatus(info.getStatus()), green(replicas(info)));
        if (healthChecker != null) {
            PodsHealthcheckInfo podsInfo = podsInfo(component, healthChecker);
            addPods(podsInfo.getPods(), podsInfo.getPodsHealth());
            return podsInfo.isHealthy() && info.getStatus() == RUNNING;
        }
        return info.getStatus() == RUNNING;
    }

    private String coloredStatus(DeploymentStatus status) {
        String statusString = status.toString().replace("_", " ");
        switch (status) {
            case RUNNING:
                return green(statusString);
            case FAILED:
            case NOT_READY:
            case BAD_HEALTHCHECK:
                return red(statusString);
            default:
                return statusString;
        }
    }

    private void addPods(List<Pod> pods, List<Boolean> podsHealth) {
        range(0, pods.size()).forEach(i -> addPodRow(pods.get(i), podsHealth.get(i)));
    }

    private void addPodRow(Pod pod, Boolean health) {
        printer.addRow(" - " + pod.getName(), "", health ? green("OK") : red("BAD"), "");
    }

    private String replicas(DeploymentInfo info) {
        return info.getAvailableReplicas() + "/" + info.getReplicas();
    }
}
