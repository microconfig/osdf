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
import static io.microconfig.utils.Logger.announce;
import static io.microconfig.utils.Logger.info;
import static java.nio.file.Files.list;
import static java.nio.file.Path.of;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public abstract class AbstractOpenShiftComponent {
    @Getter
    protected final String name;
    protected final Path configDir;
    protected final Path openShiftConfigDir;
    protected final OCExecutor oc;

    public static AbstractOpenShiftComponent fromPath(Path configDir, OCExecutor oc) {
        var openShiftConfigDir = of(configDir + "/openshift");
        for (ComponentType type : values()) {
            if (type.checkDir(openShiftConfigDir)) {
                return type.component(configDir.getFileName().toString(), configDir, openShiftConfigDir, oc);
            }
        }
        return null;
    }

    public void upload() {
        oc.executeAndReadLines("oc apply -f " + openShiftConfigDir)
                .forEach(line -> info("oc: " + line));
    }

    public void delete() {
        oc.execute("oc delete all,configmap --selector application=" + name);
        announce("Deleted " + name);
    }

    public void createConfigMap() {
        info("Creating configmap");
        var createCommand = "oc create configmap " + name + " --from-file=" + configDir;
        var labelCommand = "oc label configmap " + name + " application=" + name;
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

    protected List<OpenShiftResource> getOpenShiftResources() {
        var command = "oc get all,configmap --selector application=" + name + " -o name";
        return fromOpenShiftNotations(oc.executeAndReadLines(command), oc);
    }

    private List<OpenShiftResource> getLocalResources() {
        try (Stream<Path> stream = list(openShiftConfigDir)) {
            return stream.filter(path -> path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))
                    .map(path -> OpenShiftResource.fromPath(path, oc))
                    .collect(toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Couldn't load files from " + configDir, e);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
