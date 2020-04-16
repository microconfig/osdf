package io.microconfig.osdf.components;

import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.openshift.OpenShiftResource;
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
        oc.executeAndReadLines("oc apply -f " + configDir + "/openshift")
                .forEach(line -> info("oc: " + line));
    }

    public void delete() {
        oc.execute("oc delete all,configmap " + label());
    }

    public void deleteAll() {
        oc.execute("oc delete all,configmap -l application=" + name);
    }

    public void createConfigMap() {
        info("Creating configmap");
        var createCommand = "oc create configmap " + fullName() + " --from-file=" + configDir;
        var labelCommand = "oc label configmap " + fullName() + " application=" + name + " projectVersion=" + version;
        String output = oc.execute(createCommand);
        info("oc: " + output);
        oc.execute(labelCommand);
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
        var command = "oc get all,configmap " + label() + " -o name";
        return fromOpenShiftNotations(oc.executeAndReadLines(command), oc);
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
