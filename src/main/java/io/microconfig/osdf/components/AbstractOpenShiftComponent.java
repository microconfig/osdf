package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftResource;
import io.microconfig.utils.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.microconfig.osdf.components.ComponentType.values;
import static io.microconfig.osdf.microconfig.files.DiffFilesCollector.collector;
import static io.microconfig.osdf.openshift.OpenShiftResource.fromOpenShiftNotations;
import static io.microconfig.utils.Logger.info;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.util.Arrays.stream;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public abstract class AbstractOpenShiftComponent {
    @Getter
    protected final String name;
    @Getter
    protected final String version;
    @Getter
    protected final Path configDir;
    protected final OCExecutor oc;

    public static AbstractOpenShiftComponent fromPath(Path configDir, String version, OCExecutor oc) {
        return stream(values())
                .filter(type -> type.checkDir(of(configDir + "/openshift")))
                .findFirst()
                .map(type -> type.component(configDir.getFileName().toString(), version, configDir, oc))
                .orElse(null);
    }

    public void upload() {
        oc.execute("oc apply -f " + configDir + "/openshift")
                .throwExceptionIfError()
                .consumeOutput(Logger::info);
    }

    public void delete() {
        oc.execute("oc delete all,configmap " + label())
                .throwExceptionIfError();
    }

    public void deleteAll() {
        oc.execute("oc delete all,configmap -l application=" + name)
                .throwExceptionIfError();
    }

    public void createConfigMap() {
        info("Creating configmap");
        var createCommand = "oc create configmap " + fullName() + " --from-file=" + configDir;
        var labelCommand = "oc label configmap " + fullName() + " application=" + name + " projectVersion=" + version;
        oc.execute(createCommand)
                .throwExceptionIfError()
                .consumeOutput(Logger::info);
        oc.execute(labelCommand)
                .throwExceptionIfError();
    }

    public void deleteOldResourcesFromOpenShift() {
        List<OpenShiftResource> local = getLocalResources();
        List<OpenShiftResource> openShift = getOpenShiftResources();
        openShift.stream()
                .filter(not(OpenShiftResource::isSystemResource))
                .filter(not(local::contains))
                .forEach(OpenShiftResource::delete);
    }

    public List<Path> diffFiles() {
        return collector(configDir).collect();
    }

    public String fullName() {
        return name + "." + version;
    }

    public String getEncodedVersion() {
        return version.toLowerCase().replace(".", "-d-");
    }

    protected List<OpenShiftResource> getOpenShiftResources() {
        List<String> notations = oc.execute("oc get all,configmap " + label() + " -o name")
                .throwExceptionIfError()
                .getOutputLines();
        return fromOpenShiftNotations(notations, oc);
    }

    private List<OpenShiftResource> getLocalResources() {
        try (Stream<Path> stream = list(of(configDir + "/openshift"))) {
            return stream.filter(path -> path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))
                    .map(path -> OpenShiftResource.fromPath(path, oc))
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't load files from " + configDir, e);
        }
    }

    protected String label() {
        return "-l \"application in (" + name + "), projectVersion in (" + version +  ")\"";
    }

    @Override
    public String toString() {
        return fullName();
    }
}
