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
    private final Path configsPath = of("/tmp/configs");
    private final Path osdfPath = of("/tmp/osdf");

    @BeforeEach
    void createConfigs() throws IOException {
        ConfigUnzipper.unzip("configs.zip", configsPath);
        paths = new OSDFPaths(osdfPath);
    }

    @Test
    void statusOk() {
        defaultInstallInit(configsPath, osdfPath, paths);
        OCExecutor oc = mock(OCExecutor.class, withSettings().verboseLogging());
        when(oc.executeAndReadLines("oc get dc -l application=helloworld-springboot -o name")).thenReturn(
                List.of("deployment/helloworld-springboot.latest")
        );
        when(oc.executeAndReadLines(
                "oc get dc helloworld-springboot.latest -o custom-columns=" +
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
        when(oc.execute("oc get virtualservice helloworld-springboot -o yaml", true)).thenReturn(
                "not found"
        );
        when(oc.executeAndReadLines("oc get pods --selector name=helloworld-springboot -o name")).thenReturn(List.of(
                "pod/pod"
        ));

        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(any())).thenReturn(true);

        ColumnPrinter printer = mock(ColumnPrinter.class);
        ColumnPrinter localPrinter = mock(ColumnPrinter.class);
        when(printer.newPrinter()).thenReturn(localPrinter);



        new StatusCommand(paths, oc, healthChecker, printer).run(List.of("helloworld-springboot"));
        verify(printer).addColumns("COMPONENT", "VERSION", "TRAFFIC", "STATUS", "REPLICAS");
        verify(localPrinter).addRow("helloworld-springboot{latest}", "", "", "", "");
        verify(localPrinter).addRow(ArgumentMatchers.<Consumer<String>>any(), eq(""), eq("latest"), eq("uniform"), eq("RUNNING"), eq("1/1"));
        verify(printer).addRows(any());
        verify(printer).print();
    }
}