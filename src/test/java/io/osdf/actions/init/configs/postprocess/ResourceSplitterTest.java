package io.osdf.actions.init.configs.postprocess;

import io.osdf.context.TestContext;
import io.osdf.core.local.component.ComponentDir;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static io.osdf.actions.init.configs.postprocess.AppPostProcessor.componentPostProcessor;
import static io.osdf.context.TestContext.defaultContext;
import static io.osdf.test.ClasspathReader.classpathFile;
import static io.osdf.test.local.AppUtils.componentDirFor;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;
import static java.util.List.of;

class ResourceSplitterTest {
    private static final TestContext context = defaultContext();

    @BeforeAll
    static void initOsdf() {
        context.initDev();
    }

    @Test
    void testSplitResources() throws IOException {
        ComponentDir componentDir = componentDirWithTestResource("deployment-and-service.yaml");

        componentPostProcessor(context.getPaths()).process(componentDir);

        assertSplit(Assertions::assertTrue, componentDir,
                "deployment-and-service.yaml", of("Deployment-simple-service", "Service-simple-service")
        );
    }

    @Test
    void doNotSplitOnCommentedDashes() throws IOException {
        ComponentDir componentDir = componentDirWithTestResource("commented-split.yaml");

        componentPostProcessor(context.getPaths()).process(componentDir);

        assertSplit(Assertions::assertFalse, componentDir,
                "commented-split.yaml", of("Kind1-name1", "Kind2-name2")
        );
    }

    @Test
    void splitWithEmptySplits() throws IOException {
        ComponentDir componentDir = componentDirWithTestResource("empty-splits.yaml");

        componentPostProcessor(context.getPaths()).process(componentDir);

        assertSplit(Assertions::assertTrue, componentDir,
                "empty-splits.yaml", of("Kind1-name1", "Kind2-name2")
        );
    }

    @Test
    void supportAnyExtension() throws IOException {
        ComponentDir componentDir = componentDirFor("simple-service");
        copy(classpathFile("resources/deployment-and-service.yaml"), componentDir.getPath("resources/deployment-and-service.any"));

        componentPostProcessor(context.getPaths()).process(componentDir);

        assertSplit(Assertions::assertTrue, componentDir,
                "deployment-and-service.any", of("Deployment-simple-service", "Service-simple-service")
        );
    }

    void assertSplit(Consumer<Boolean> splitCheck, ComponentDir componentDir, String source, List<String> children) {
        splitCheck.accept(!exists(componentDir.getPath("resources/" + source)));
        String[] nameAndExtension = source.split("\\.");
        String name = nameAndExtension[0];
        String extension = nameAndExtension[1];
        children.forEach(child ->
                splitCheck.accept(exists(componentDir.getPath("resources/" + name + "-" + child + "." + extension)))
        );
    }

    private ComponentDir componentDirWithTestResource(String resource) throws IOException {
        ComponentDir componentDir = componentDirFor("simple-service");
        copy(classpathFile("resources/" + resource), componentDir.getPath("resources/" + resource));
        return componentDir;
    }
}