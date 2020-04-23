package io.microconfig.osdf.openshift;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.dump;
import static io.microconfig.utils.Logger.info;
import static java.util.List.of;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableList;

@EqualsAndHashCode
@RequiredArgsConstructor
public class OpenShiftResource {
    private static final List<String> SYSTEM_RESOURCES = of("replicationcontroller", "pod");

    private final String kind;
    private final String name;
    private final OCExecutor oc;

    public static List<OpenShiftResource> fromOpenShiftNotations(List<String> lines, OCExecutor oc) {
        return lines
                .stream()
                .filter(not(String::isEmpty))
                .map(notation -> fromOpenShiftNotation(notation, oc))
                .collect(toUnmodifiableList());
    }

    public static OpenShiftResource fromOpenShiftNotation(String notation, OCExecutor oc) {
        String[] split = notation.split("/");
        if (split.length != 2) {
            throw new RuntimeException("Wrong OpenShift Notation format " + notation);
        }
        String fullKind = split[0];
        String name = split[1];

        String[] fullKindSplit = fullKind.split("\\.");
        String kind = fullKindSplit[0].toLowerCase();
        return new OpenShiftResource(kind, name, oc);
    }

    public static OpenShiftResource fromPath(Path path, OCExecutor oc) {
        try {
            Map<String, Object> resource = new Yaml().load(new FileInputStream(path.toString()));

            String kind = ((String) resource.get("kind")).toLowerCase();

            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) resource.get("metadata");
            String name = (String) metadata.get("name");
            return new OpenShiftResource(kind, name, oc);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Couldn't find resource at " + path, e);
        }
    }

    public static void uploadResource(OCExecutor oc, Object resource) {
        Path tmpPath = Path.of("/tmp/resource.yaml");
        dump(resource, tmpPath);
        oc.execute("oc apply -f " + tmpPath);
    }

    public void delete() {
        var command = "oc delete " + kind + " " + name;
        oc.execute(command);
        info("Deleted: " + toString());
    }

    public boolean isSystemResource() {
        return SYSTEM_RESOURCES.contains(kind);
    }

    @Override
    public String toString() {
        return kind + "/" + name;
    }
}
