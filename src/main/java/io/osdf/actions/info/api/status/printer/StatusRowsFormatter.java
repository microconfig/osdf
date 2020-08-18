package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.description.CoreDescription;
import io.osdf.core.application.core.files.ApplicationFiles;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.microconfig.utils.ConsoleColor.green;

@RequiredArgsConstructor
public class StatusRowsFormatter {
    @Getter
    private final ColumnPrinter printer;

    public static StatusRowsFormatter formatter(ColumnPrinter printer) {
        return new StatusRowsFormatter(printer);
    }

    public void addMainRow(ApplicationFiles files, CoreDescription coreDescription, String coloredStatus, String replicas) {
        YamlObject yaml = files.deployProperties();
        printer.addRow(green(files.name()),
                green(formatVersions(coreDescription.getAppVersion(), yaml.get("app.version"))),
                green(formatVersions(coreDescription.getConfigVersion(), yaml.get("config.version"))),
                coloredStatus,
                green(replicas));
    }

    public void addNotFoundRow(ApplicationFiles files, String coloredStatus) {
        YamlObject yaml = files.deployProperties();
        printer.addRow(green(files.name()),
                green(formatVersions("-", yaml.get("app.version"))),
                green(formatVersions("-", yaml.get("config.version"))),
                coloredStatus,
                green("-"));
    }

    private String formatVersions(String remote, String local) {
        if (remote.equalsIgnoreCase(local)) return remote;
        return remote + " [" + local + "]";
    }
}
