package io.osdf.common.utils;

public class OCCommands {
    public static String deploymentInfoCustomColumns() {
        return "-o custom-columns=" +
                "replicas:.spec.replicas," +
                "current:.status.replicas," +
                "available:.status.availableReplicas," +
                "unavailable:.status.unavailableReplicas," +
                "ready:.status.readyReplicas," +
                "projectVersion:.metadata.labels.projectVersion," +
                "configVersion:.metadata.labels.configVersion," +
                "configHash:.metadata.labels.configHash";
    }
}
