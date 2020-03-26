package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.printer.ColumnPrinter;
import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static io.microconfig.osdf.utils.InstallInitUtils.defaultInstallInit;
import static java.nio.file.Path.of;
import static org.mockito.Mockito.*;

class StatusCommandTest {
    private OSDFPaths paths;
    private Path configsPath = of("/tmp/configs");
    private Path osdfPath = of("/tmp/osdf");

    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", configsPath);
        paths = new OSDFPaths(osdfPath);
    }

    @Test
    void statusOk() {
        defaultInstallInit(configsPath, osdfPath, paths);
        OCExecutor oc = mock(OCExecutor.class);
        when(oc.executeAndReadLines(
                "oc get dc helloworld-springboot -o custom-columns=" +
                        "replicas:.spec.replicas," +
                        "current:.status.replicas," +
                        "available:.status.availableReplicas," +
                        "unavailable:.status.unavailableReplicas," +
                        "projectVersion:.metadata.labels.projectVersion," +
                        "configVersion:.metadata.labels.configVersion",
                true)).thenReturn(List.of(
                "replicas   current   available   unavailable   projectVersion   configVersion",
                "1          1         1           0             latest           local"
        ));
        when(oc.executeAndReadLines("oc get pods --selector name=helloworld-springboot -o name")).thenReturn(List.of(
                "pod/pod"
        ));

        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(any())).thenReturn(true);

        ColumnPrinter printer = mock(ColumnPrinter.class);


        new StatusCommand(paths, oc, healthChecker, printer).run(List.of("helloworld-springboot"));
        verify(printer).addColumns("COMPONENT", "STATUS", "REPLICAS", "VERSION", "CONFIGS");
        verify(printer).addRow("helloworld-springboot", "RUNNING", "1/1", "latest", "local");
        verify(printer).addRow(ArgumentMatchers.<Consumer<String>>any(), eq(" - pod"), eq("OK"), eq(""), eq(""), eq(""));
        verify(printer).print();
    }
}