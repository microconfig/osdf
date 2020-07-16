package io.osdf.actions.info.api.status.printer;

import io.osdf.actions.info.printer.ColumnPrinter;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.job.JobApplication;
import io.osdf.core.application.service.ServiceApplication;
import io.osdf.core.connection.cli.ClusterCli;
import io.osdf.core.local.component.MicroConfigComponentDir;
import io.osdf.test.cluster.JobAppApi;
import io.osdf.test.cluster.ServiceApi;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

import static io.microconfig.utils.ConsoleColor.green;
import static io.osdf.actions.info.api.status.printer.StatusPrinter.statusPrinter;
import static io.osdf.actions.info.printer.ColumnPrinter.printer;
import static io.osdf.actions.init.configs.postprocess.metadata.MetadataCreatorImpl.metadataCreator;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.JOB;
import static io.osdf.actions.init.configs.postprocess.types.MetadataType.SERVICE;
import static io.osdf.core.application.core.files.ApplicationFilesImpl.applicationFiles;
import static io.osdf.core.application.job.JobApplication.jobApp;
import static io.osdf.core.application.service.ServiceApplication.serviceApplication;
import static io.osdf.core.local.component.MicroConfigComponentDir.componentDir;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.cluster.JobAppApi.jobAppApi;
import static io.osdf.test.cluster.ServiceApi.serviceApi;
import static java.lang.System.setOut;
import static java.nio.file.Files.createDirectories;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusPrinterTest {
    @TempDir
    Path tempDir;

    @Test
    void testServiceOk() {
        ServiceApi serviceApi = serviceApi("test");

        String actual = getOutput(() ->
                statusPrinter(serviceApi, printer(), false)
                        .checkStatusAndPrint(of(service("test", serviceApi)), emptyList()));

        ColumnPrinter printer = getPrinter();
        printer.addRow(green("test"), green("latest"), green("master"), green("READY"), green("2/2"));
        String expected = getOutput(printer::print);

        assertEquals(expected, actual);
    }

    @Test
    void testJobSucceeded() {
        JobAppApi jobAppApi = jobAppApi("test");

        String actual = getOutput(() -> statusPrinter(jobAppApi, printer(), false)
                .checkStatusAndPrint(emptyList(), of(job("test", jobAppApi))));

        ColumnPrinter printer = getPrinter();
        printer.addRow(green("test"), green("latest"), green("master"), green("SUCCEEDED"), green("-"));
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

    private ServiceApplication service(String name, ClusterCli cli) throws IOException {
        Path serviceDir = classpathFile("components/simple-service");
        Path destination = Path.of(tempDir + "/" + name);
        createDirectories(destination);

        copyDirectory(serviceDir.toFile(), destination.toFile());

        MicroConfigComponentDir componentDir = componentDir(destination);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(SERVICE, componentDir);
        return serviceApplication(files, cli);
    }

    private JobApplication job(String name, ClusterCli cli) throws IOException {
        Path serviceDir = classpathFile("components/simple-job");
        Path destination = Path.of(tempDir + "/" + name);
        createDirectories(destination);

        copyDirectory(serviceDir.toFile(), destination.toFile());

        MicroConfigComponentDir componentDir = componentDir(destination);
        ApplicationFiles files = applicationFiles(componentDir);
        metadataCreator().create(JOB, componentDir);
        return jobApp(files, cli);
    }
}