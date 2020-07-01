package io.microconfig.osdf.commands;

import io.osdf.core.connection.cli.openshift.OpenShiftCli;
import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.common.utils.TestContext;
import io.osdf.actions.info.api.status.StatusCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.actions.info.printer.ColumnPrinter.printer;
import static io.osdf.common.utils.MockObjects.loggedInOc;
import static io.osdf.common.utils.OCCommands.deploymentInfoCustomColumns;
import static io.osdf.common.utils.TestContext.defaultContext;
import static io.microconfig.utils.ConsoleColor.green;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class StatusCommandTest {
    private final TestContext context = defaultContext();

    private final String COMPONENT_NAME = "helloworld-springboot";
    private final String COMPONENT_VERSION = "latest";

    @BeforeEach
    void prepareEnv() throws IOException {
        context.initDev();
    }

    @Test
    void statusOk() {
        OpenShiftCli oc = mockOc();

        String actualOut = getStatusOutput(oc, printer());
        String expectedOutput = getExpectedOutput();
        assertEquals(expectedOutput, actualOut);
    }

    private OpenShiftCli mockOc() {
        OpenShiftCli oc = loggedInOc();
        when(oc.execute("get dc " + COMPONENT_NAME + " " + deploymentInfoCustomColumns())).thenReturn(output(
                "replicas   current   available   unavailable   projectVersion   configVersion  configHash" + "\n" +
                "1          1         1           0             " + COMPONENT_VERSION + "           local   hash"
        ));
        when(oc.execute("get virtualservice " + COMPONENT_NAME + " -o yaml"))
                .thenReturn(output("not found"));
        when(oc.execute("get pods -l \"application in (" + COMPONENT_NAME + "), projectVersion in (" + COMPONENT_VERSION + ")\" -o name"))
                .thenReturn(output("pod/pod"));
        return oc;
    }

    private String getExpectedOutput() {
        ByteArrayOutputStream expectedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(expectedOut));

        ColumnPrinter printer = printer();
        printer.addColumns("COMPONENT", "VERSION", "CONFIGS", "STATUS", "REPLICAS");
        printer.addRow(green(COMPONENT_NAME), green(COMPONENT_VERSION), green("local"), green("RUNNING"), green("1/1"));
        printer.print();
        return expectedOut.toString();
    }

    private String getStatusOutput(OpenShiftCli oc, ColumnPrinter printer) {
        ByteArrayOutputStream actualOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(actualOut));
        new StatusCommand(context.getPaths(), oc, printer, false).run(List.of(COMPONENT_NAME));
        return actualOut.toString();
    }
}