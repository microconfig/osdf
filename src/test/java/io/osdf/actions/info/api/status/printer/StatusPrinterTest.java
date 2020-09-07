package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.application.core.Application;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.test.cluster.TestCli;
import io.osdf.test.cluster.api.JobAppApi;
import io.osdf.test.cluster.api.ServiceApi;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static io.microconfig.utils.ConsoleColor.green;
import static io.osdf.actions.info.api.status.printer.StatusPrinter.statusPrinter;
import static io.osdf.actions.info.printer.ColumnPrinter.printer;
import static io.osdf.core.application.job.JobApplication.jobApp;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.test.cluster.api.JobAppApi.jobAppApi;
import static io.osdf.test.cluster.api.ServiceApi.serviceApi;
import static io.osdf.test.local.AppUtils.applicationFilesFor;
import static java.lang.System.setOut;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
class StatusPrinterTest {
    @Test
    void testServiceOk() {
        ServiceApi serviceApi = serviceApi("test");

        assertAppStatusRows(serviceApi, service(serviceApi), of(
                green("test"), green("latest"), green("master"), green("READY"), green("2/2")
        ));
    }

    @Test
    void testServiceNotFound() {
        ServiceApi serviceApi = serviceApi("test");
        serviceApi.getConfigMapApi().exists(false);

        assertAppStatusRows(serviceApi, service(serviceApi), of(
                green("test"), green("- [latest]"), green("- [master]"), "NOT FOUND", green("-")
        ));
    }

    @Test
    void testJobSucceeded() {
        JobAppApi jobAppApi = jobAppApi("test");

        assertAppStatusRows(jobAppApi, job(jobAppApi), of(
                green("test"), green("latest"), green("master"), green("SUCCEEDED"), green("-")
        ));
    }

    void assertAppStatusRows(TestCli cli, Application app, List<String> row) {
        String actual = getOutput(() ->
                statusPrinter(cli, printer(), false)
                        .checkStatusAndPrint(of(app)));

        ColumnPrinter printer = getPrinter();
        printer.addRow(row.toArray(String[]::new));
        String expected = getOutput(printer::print);
        assertEquals(expected, actual);
    }

    @SneakyThrows
    private String getOutput(Executable executable) {
        ByteArrayOutputStream printContent = new ByteArrayOutputStream();
        setOut(new PrintStream(printContent));
        executable.execute();
        return printContent.toString();
    }

    private ColumnPrinter getPrinter() {
        ColumnPrinter printer = printer();
        printer.addColumns("COMPONENT", "VERSION", "CONFIGS", "STATUS", "REPLICAS");
        return printer;
    }

    private ServiceApplication service(ClusterCli cli) {
        ApplicationFiles files = applicationFilesFor("simple-service", "/test");
        return serviceApplication(files, cli);
    }

    private JobApplication job(ClusterCli cli) {
        ApplicationFiles files = applicationFilesFor("simple-job", "/test");
        return jobApp(files, cli);
    }
}