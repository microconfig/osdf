package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.printer.ColumnPrinter;
import io.microconfig.osdf.utils.ConfigUnzipper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.utils.InstallInitUtils.defaultInstallInit;
import static java.nio.file.Path.of;
import static org.mockito.Mockito.*;

class PodsCommandTest {
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
        when(oc.executeAndReadLines("oc get pods --selector name=helloworld-springboot -o name")).thenReturn(List.of(
                "pod/pod"
        ));

        HealthChecker healthChecker = mock(HealthChecker.class);
        when(healthChecker.check(any())).thenReturn(true);

        ColumnPrinter printer = mock(ColumnPrinter.class);


        new PodsCommand(paths, oc, healthChecker, printer).show(List.of("helloworld-springboot"));
        verify(printer).addColumns("COMPONENT", "POD", "HEALTH");
        verify(printer).addRow("helloworld-springboot", "pod", "OK");
        verify(printer).print();
    }
}