package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.printer.ColumnPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.microconfig.osdf.utils.InstallInitUtils.createConfigsAndInstallInit;
import static org.mockito.Mockito.*;

class PodsCommandTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = createConfigsAndInstallInit();
    }

    @Test
    void statusOk() {
        OCExecutor oc = mock(OCExecutor.class);
        when(oc.executeAndReadLines("oc get pods -l \"application in (helloworld-springboot), projectVersion in (latest)\" -o name")).thenReturn(List.of(
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