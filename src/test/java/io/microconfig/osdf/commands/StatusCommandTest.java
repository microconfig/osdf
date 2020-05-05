package io.microconfig.osdf.commands;

import io.microconfig.osdf.components.checker.HealthChecker;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.printers.ColumnPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static io.microconfig.osdf.printers.ColumnPrinter.printer;
import static io.microconfig.osdf.utils.MockObjects.loggedInOc;
import static io.microconfig.utils.ConsoleColor.green;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StatusCommandTest {
    private OSDFPaths paths;

    @BeforeEach
    void createConfigs() throws IOException {
        paths = null; //TODO
    }

    @Test
    void statusOk() {
        OCExecutor oc = loggedInOc();
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

        String actualOut = getStatusOutput(oc, healthChecker, printer());
        String expectedOutput = getExpectedOutput();
        assertEquals(expectedOutput, actualOut);
    }

    private String getExpectedOutput() {
        ByteArrayOutputStream expectedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(expectedOut));

        ColumnPrinter printer = printer();
        printer.addColumns("COMPONENT", "VERSION", "STATUS", "REPLICAS");
        printer.addRow(green("helloworld-springboot"), green("latest"), green("RUNNING"), green("1/1"));
        printer.print();
        return expectedOut.toString();
    }

    private String getStatusOutput(OCExecutor oc, HealthChecker healthChecker, ColumnPrinter printer) {
        ByteArrayOutputStream actualOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOut));
        new StatusCommand(paths, oc, printer, true).run(List.of("helloworld-springboot")); //TODO
        return actualOut.toString();
    }
}