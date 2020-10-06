package io.osdf.actions.init.configs.postprocess.template;

import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.core.local.component.ComponentDir;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import static io.osdf.actions.init.configs.postprocess.template.YamlTemplateResolver.yamlTemplateResolver;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Files.list;
import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK;

public class ResourceTemplatePostProcessor {
    private final Yaml yamlDumper = yamlDumper();

    public static ResourceTemplatePostProcessor resourceTemplatePostProcessor() {
        return new ResourceTemplatePostProcessor();
    }

    public void postProcess(ComponentDir componentDir) {
        YamlTemplateResolver resolver = yamlTemplateResolver(yaml(componentDir.getPath("deploy.yaml")));

        try (Stream<Path> resources = list(componentDir.getPath("resources"))) {
            resources.forEach(resource -> doPostProcess(resource, resolver));
        } catch (IOException e) {
            throw new PossibleBugException("Couldn't list resources in " + componentDir.name() + " component");
        }
    }

    private void doPostProcess(Path resource, YamlTemplateResolver resolver) {
        Map<String, Object> yaml = yaml(resource).getYaml();
        Map<String, Object> resolved = resolver.resolve(yaml);
        writeStringToFile(resource, yamlDumper.dump(resolved));
    }

    private Yaml yamlDumper() {
        DumperOptions options = new DumperOptions();
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(BLOCK);
        return new Yaml(options);
    }
}
