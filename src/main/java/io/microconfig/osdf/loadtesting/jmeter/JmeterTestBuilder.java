package io.microconfig.osdf.loadtesting.jmeter;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static io.microconfig.osdf.utils.FileUtils.*;
import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class JmeterTestBuilder {
    private final Path componentsPath;
    private final Path componentTestPath;
    private final Map<String, String> componentsRoutes;

    public static JmeterTestBuilder jmeterConfigBuilder(String componentName, Path componentsPath,
                                                        Map<String, String> componentsRoutes) {
        Path componentTestPath = initNewTestPath(componentsPath, componentName);
        return new JmeterTestBuilder(componentsPath, componentTestPath, componentsRoutes);
    }

    private static Path initNewTestPath(Path jmeterComponentsPath, String name) {
        Path testConfigsPath = Path.of(jmeterComponentsPath + "/templates/tests");
        Path newTestConfigPath = Path.of(jmeterComponentsPath + "/" + name + "/tests");
        copyDirectory(testConfigsPath, newTestConfigPath);
        return newTestConfigPath;
    }

    public Path build() {
        Path testTemplatePath = of(componentTestPath + "/jmetertest.jmx");
        String testTemplate = readAll(testTemplatePath);

        Path userTestConfigPath = Path.of(componentsPath + "/config/jmeter-test-config.yaml");
        Map<String, Object> userConfig = loadFromPath(userTestConfigPath);
        List<Map<String, Object>> requests = getListOfMaps(userConfig, "requests");

        testTemplate = buildSimpleRequestTemplate(testTemplate, requests);
        testTemplate = prepareMainConfigParams(userConfig, testTemplate);
        writeStringToFile(testTemplatePath, testTemplate);
        return testTemplatePath;
    }

    private String buildSimpleRequestTemplate(String testTemplate, List<Map<String, Object>> requests) {
        for (Map<String, Object> request : requests) {
            String httpTestName = getFirstKey(request);
            Map<String, Object> requestConfig = getMap(request, httpTestName);
            requestConfig.put("testname", httpTestName);
            String simpleRequest = prepareSimpleHttpRequest(requestConfig);
            testTemplate = addAndNotReplace("#HTTP_SIMPLE_REQUEST#", simpleRequest, testTemplate);
        }
        return testTemplate.replaceAll("#HTTP_SIMPLE_REQUEST#", "");
    }

    private String prepareMainConfigParams(Map<String, Object> params, String testTemplate) {
        for (Map.Entry<String, Object> param : params.entrySet()) {
            testTemplate = replaceParam(testTemplate, param.getKey(), String.valueOf(param.getValue()));
        }
        return testTemplate;
    }

    private String addAndNotReplace(String param, String value, String testTemplate) {
        StringBuilder templateBuilder = new StringBuilder(testTemplate);
        int index = testTemplate.indexOf(param);
        templateBuilder.insert(index, value + "\n");
        testTemplate = templateBuilder.toString();
        return testTemplate;
    }

    private String prepareSimpleHttpRequest(Map<String, Object> requestConfig) {
        String httpTemplate = readAll(of(componentTestPath + "/http-simple-template.xml"));
        httpTemplate = prepareRequestParams(componentTestPath, requestConfig, httpTemplate);
        for (Map.Entry<String, Object> param : requestConfig.entrySet()) {
            httpTemplate = replaceParam(httpTemplate, param.getKey(), String.valueOf(param.getValue()));
        }
        httpTemplate = insertDomainName(httpTemplate, String.valueOf(requestConfig.get("component")));
        return httpTemplate;
    }

    private String prepareRequestParams(Path componentTestPath, Map<String, Object> requestConfig, String httpTemplate) {
        if (requestConfig.containsKey("params")) {
            List<Map<String, Object>> params = getListOfMaps(requestConfig, "params");
            if(params == null) throw  new RuntimeException("Params in test-config.yaml is null");
            for (Map<String, Object> param : params) {
                String paramsTemplate = readAll(of(componentTestPath + "/params-template.xml"));
                String name = getFirstKey(param);
                String value = String.valueOf(param.get(name));
                paramsTemplate = replaceParam(paramsTemplate, "name", name);
                paramsTemplate = replaceParam(paramsTemplate, "value", value);
                httpTemplate = addAndNotReplace("#PARAMS_TEMPLATE#", paramsTemplate, httpTemplate);
            }
        }
        return httpTemplate.replaceAll("#PARAMS_TEMPLATE#", "");
    }

    private String replaceParam(String template, String param, String value) {
        String regex = "#" + param.toUpperCase() + "#";
        template = template.replaceAll(regex, value);
        return template;
    }

    private String insertDomainName(String httpTemplate, String componentName) {
        String componentRoute = componentsRoutes.get(componentName);
        if (componentRoute == null) throw new RuntimeException("Component: " + componentName + "hasn't route to test");
        httpTemplate = httpTemplate.replaceAll("#DOMAIN#", componentRoute);
        return httpTemplate;
    }

    private String getFirstKey(Map<String, Object> map) {
        return map.keySet()
                .stream()
                .findFirst()
                .orElseThrow();
    }
}
