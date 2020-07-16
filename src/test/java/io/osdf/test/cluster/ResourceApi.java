package io.osdf.test.cluster;

import io.osdf.core.cluster.resource.ClusterResourceImpl;
import io.osdf.core.connection.cli.CliOutput;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.util.regex.Matcher;

import static io.osdf.core.cluster.resource.ClusterResourceImpl.fromPath;
import static io.osdf.core.connection.cli.CliOutput.errorOutput;
import static io.osdf.core.connection.cli.CliOutput.output;
import static io.osdf.test.cluster.TestCliUtils.*;
import static java.util.List.of;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.regex.Pattern.compile;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ResourceApi extends TestCli {
    private final String kind;
    private final String name;

    private boolean immutableError = false;
    @Getter @Setter
    private boolean exists = true;
    @Getter
    private int resourceVersion = current().nextInt();

    public static ResourceApi resourceApi(String kind, String name) {
        return new ResourceApi(kind, name);
    }

    @Override
    public CliOutput execute(String command) {
        return executeUsing(command, of(this::apply, this::delete, this::get));
    }

    private CliOutput apply(String command) {
        Matcher matcher = compile("apply -f (.*)").matcher(command);
        if (!matcher.matches()) return unknown();

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

    private CliOutput delete(String command) {
        Matcher matcher = compile("delete (.*)\\s(.*)").matcher(command);
        if (!matcher.matches()) return unknown();

        String kind = matcher.group(1);
        String name = matcher.group(2);
        if (!kind.equals(this.kind) || !name.equals(this.name)) return errorOutput("not found", 1);

        if (exists) exists = false;
        if (immutableError) immutableError = false;

        return output("deleted");
    }

    private CliOutput get(String command) {
        Matcher matcher = compile("get (.*)\\s(.*)").matcher(command);
        if (!matcher.matches()) return unknown();

        String kind = matcher.group(1);
        String name = matcher.group(2);
        if (!kind.equals(this.kind) || !name.equals(this.name)) return errorOutput("not found", 1);

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
