package io.osdf.test.cluster.api;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;
import io.osdf.test.cluster.TestCli;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.regex.Matcher;

import static io.osdf.common.utils.StringUtils.castToInteger;
import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.api.ResourceApi.resourceApi;
import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.List.of;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class DeploymentApi extends TestCli {
    private final String kind;
    private final String name;
    private final List<String> labels;
    @Getter
    private final PropertiesApi propertiesApi;
    @Getter
    private final ResourceApi deploymentResourceApi;

    @Getter
    private List<String> pods = of("pod/pod1", "pod/pod2");

    public static DeploymentApi deploymentApi(String kind, String name) {
        List<String> labels = of("app:" + name);

        PropertiesApi propertiesApi = PropertiesApi.propertiesApi(kind, name);
        propertiesApi.add(kind.equals("deployment") ? "spec.selector.matchLabels" : "spec.selector", "map[" + labels.get(0) + "]");
        propertiesApi.add("spec.replicas", "2");
        propertiesApi.add("status.replicas", "2");
        propertiesApi.add("status.readyReplicas", "2");
        propertiesApi.add("status.availableReplicas", "2");

        return new DeploymentApi(kind, name, labels, propertiesApi, resourceApi(kind, name));
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .executor(deploymentResourceApi::execute)
                .executor(propertiesApi::execute)
                .pattern("get pods -l \\\"(.*)\\\" -o name", this::pods)
                .pattern("scale (.*?) (.*?) --replicas=(.*)", this::scale)
                .build().execute(command);
    }

    private CliOutput pods(Matcher matcher) {
        boolean found = stream(matcher.group(1).split(":"))
                .map(label -> label.replace(" in ", ":"))
                .map(label -> label.replace("(", ""))
                .map(label -> label.replace(")", ""))
                .map(String::trim)
                .allMatch(labels::contains);
        if (!found) return output("");
        return output(join("\n", pods));
    }

    private CliOutput scale(Matcher matcher) {
        String kind = matcher.group(1);
        String name = matcher.group(2);
        Integer replicas = castToInteger(matcher.group(3));

        if (!kind.equals(this.kind) || !name.equals(this.name)) return errorOutput("not found", 1);
        if (replicas == null) return errorOutput("replicas must be int", 1);

        pods = generatePods(replicas);
        return output("scaled");
    }

    private List<String> generatePods(int num) {
        return range(1, num + 1)
                .mapToObj(i -> "pod/pod" + i)
                .collect(toUnmodifiableList());
    }
}
