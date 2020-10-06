package io.osdf.actions.init.configs.postprocess.template;

import io.osdf.common.yaml.YamlObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.osdf.actions.init.configs.postprocess.template.YamlTemplateResolver.yamlTemplateResolver;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.yaml.YamlObject.yaml;
import static io.osdf.test.ClasspathReader.classpathFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlTemplateResolverTest {
    @Test
    void testSet_WithoutKey() {
        templateTest("set.yaml");
    }

    @Test
    void testSet_WithKey() {
        templateTest("set-withkey.yaml");
    }

    @Test
    void testSet_notFound() {
        templateTest("set-notFound.yaml");
    }

    @Test
    void testIf_onKeyExist() {
        templateTest("if.yaml");
    }

    @Test
    void testIf_onBooleanValue() {
        templateTest("if-boolean.yaml");
    }

    @Test
    void testIf_nested() {
        templateTest("if-nested.yaml");
    }

    @Test
    void testInclude() {
        templateTest("include.yaml");
    }

    @Test
    void testInclude_keyNotFound() {
        templateTest("include-notFound.yaml");
    }

    @Test
    void testAdd() {
        templateTest("add.yaml");
    }

    private void templateTest(String testFile) {
        String content = readAll(classpathFile("template-engine/" + testFile));
        String[] sourceAndTemplateAndResult = content.split("---");
        YamlObject keySource = yaml(sourceAndTemplateAndResult[0]);
        Map<String, Object> template = yaml(sourceAndTemplateAndResult[1]).getYaml();
        Map<String, Object> expected = yaml(sourceAndTemplateAndResult[2]).getYaml();
        Map<String, Object> resolved = yamlTemplateResolver(keySource).resolve(template);
        assertEquals(expected, resolved);
    }
}