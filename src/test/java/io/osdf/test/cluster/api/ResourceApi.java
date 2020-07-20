package io.osdf.test.cluster.api;

import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.CliOutput;
import io.osdf.test.cluster.TestApiExecutor;
import io.osdf.test.cluster.TestCli;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.util.regex.Matcher;

import static io.osdf.core.cluster.resource.ClusterResourceImpl.fromPath;
import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.TestCliUtils.unknown;
import static java.util.concurrent.ThreadLocalRandom.current;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ResourceApi extends TestCli {
    private final String kind;
    private final String name;

    @Getter @Setter
    private boolean exists = true;
    @Getter
    private int resourceVersion = current().nextInt();
    private boolean immutableError = false;

    public static ResourceApi resourceApi(String kind, String name) {
        return new ResourceApi(kind, name);
    }

    @Override
    public CliOutput execute(String command) {
        return TestApiExecutor.builder()
                .pattern("apply -f (.*)", this::apply)
                .pattern("delete (.*)\\s(.*)", this::delete)
                .pattern("get ([^\\s]*?) ([^\\s]*?)$", this::get)
                .build().execute(command);
    }

    private CliOutput apply(Matcher matcher) {
        Path resourcePath = Path.of(matcher.group(1));
        ClusterResourceImpl resource = fromPath(resourcePath);
        if (!resource.kind().equals(kind) || !resource.name().equals(name)) return output("ok");

        if (exists && immutableError) {
            immutableError = false;
            return errorOutput("field is immutable", 1);
        }
        update();
        return output("ok");
    }

    private CliOutput delete(Matcher matcher) {
        String kind = matcher.group(1);
        String name = matcher.group(2);
        if (!kind.equals(this.kind) || !name.equals(this.name)) return errorOutput("not found", 1);

        if (exists) exists = false;
        if (immutableError) immutableError = false;

        return output("deleted");
    }

    private CliOutput get(Matcher matcher) {
        String kind = matcher.group(1);
        String name = matcher.group(2);
        if (!kind.equals(this.kind) || !name.equals(this.name)) return unknown();

        if (!exists) return errorOutput("not found", 1);
        return output(kind + "/" + name);
    }

    public void update() {
        resourceVersion = current().nextInt();
        exists = true;
        immutableError = false;
    }

    public ResourceApi expectImmutableChange() {
        immutableError = true;
        return this;
    }
}
