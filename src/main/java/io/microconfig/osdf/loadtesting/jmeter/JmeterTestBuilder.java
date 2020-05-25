package io.microconfig.osdf.loadtesting.jmeter;

import lombok.RequiredArgsConstructor;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class JmeterTestBuilder {
    private final Path componentsPath;
    private final Map<String, String> componentsRoutes;
    private final Map<String, HeaderManager> headerManagerMap;

    public static JmeterTestBuilder jmeterConfigBuilder(Path componentsPath, Map<String, String> componentsRoutes) {
        return new JmeterTestBuilder(componentsPath, componentsRoutes, new HashMap<>());
    }

    public Path build() {
        Path testTemplatePath = of(componentsPath + "/config/jmetertest.jmx");
        Path userTestConfigPath = Path.of(componentsPath + "/config/jmeter-test-config.yaml");
        Map<String, Object> userConfig = loadFromPath(userTestConfigPath);
        prepareTestPlan(userConfig, testTemplatePath);
        return testTemplatePath;
    }

    private void prepareTestPlan(Map<String, Object> userConfig, Path testTemplatePath) {
        List<Map<String, Object>> requests = getListOfMaps(userConfig, "requests");
        List<HTTPSamplerProxy> httpSamplers = prepareRequests(requests);
        LoopController loopController = prepareLoopController();
        ThreadGroup threadGroup = prepareThreadGroup(userConfig, loopController);

        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        HashTree httpSamplerHashTree = new ListedHashTree();
        for (HTTPSamplerProxy httpSampler : httpSamplers) {
            httpSamplerHashTree.add(httpSampler, headerManagerMap.get(httpSampler.getName()));
        }
        threadGroupHashTree.add(httpSamplerHashTree);

        try {
            JMeterUtils.setJMeterHome(Objects.requireNonNull(getClass().getClassLoader().getResource("jmeter")).getPath());
            SaveService.saveTree(testPlanTree, new FileOutputStream(testTemplatePath.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Error in saving jmetertest.jmx", e);
        }
    }

    private HeaderManager prepareHeaderManager(String httpRequestName) {
        HeaderManager manager = new HeaderManager();
        manager.add(new Header("content-type", "application/json"));
        manager.setName(httpRequestName + "content-type");
        manager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        manager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        return manager;
    }

    private ThreadGroup prepareThreadGroup(Map<String, Object> userConfig, LoopController loopController) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Jmeter Test Group");
        threadGroup.setNumThreads(Integer.parseInt(checkForNullAndReturn(userConfig, "numberOfThreads")));
        threadGroup.setRampUp(Integer.parseInt(checkForNullAndReturn(userConfig, "rampUpPeriod")));
        threadGroup.setScheduler(true);
        threadGroup.setDuration(Integer.parseInt(checkForNullAndReturn(userConfig, "duration")));
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        return threadGroup;
    }

    private LoopController prepareLoopController() {
        LoopController loopController = new LoopController();
        loopController.setContinueForever(false);
        loopController.setLoops(-1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        return loopController;
    }

    private List<HTTPSamplerProxy> prepareRequests(List<Map<String, Object>> requests) {
        return requests.stream()
                .map(this::prepareSimpleRequest)
                .collect(Collectors.toList());
    }

    private HTTPSamplerProxy prepareSimpleRequest(Map<String, Object> request) {
        String httpRequestName = getFirstKey(request);
        Map<String, Object> requestConfig = getMap(request, httpRequestName);

        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        if (requestConfig.containsKey("params")) {
            Map<String, String> params = prepareRequestArguments(requestConfig);
            params.forEach((key, value) -> httpSampler.addArgument(key, value, "="));
        }
        String method = checkForNullAndReturn(requestConfig, "method");
        if (method.equals("POST")) {
            String body = checkForNullAndReturn(requestConfig, "body");
            httpSampler.setPostBodyRaw(true);
            httpSampler.addNonEncodedArgument("Body Data", body, "=");
        }
        httpSampler.setDomain(prepareDomain(requestConfig));
        httpSampler.setName(httpRequestName);
        httpSampler.setMethod(method);
        httpSampler.setPath(checkForNullAndReturn(requestConfig, "path"));
        httpSampler.setProtocol(checkForNullAndReturn(requestConfig, "protocol"));

        httpSampler.setUseKeepAlive(true);
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        HeaderManager headerManager = prepareHeaderManager(httpRequestName);
        headerManagerMap.put(httpRequestName, headerManager);
        if (requestConfig.containsKey("headers")) {
            getListOfMaps(requestConfig, "headers").forEach(header -> {
                String name = getFirstKey(header);
                String value = String.valueOf(header.get(name));
                headerManager.add(new Header(name, value));
            });
            headerManagerMap.put(httpRequestName, headerManager);
        }
        return httpSampler;
    }

    private String prepareDomain(Map<String, Object> requestConfig) {
        if (requestConfig.containsKey("component"))
            return componentsRoutes.get(String.valueOf(requestConfig.get("component")));
        if (requestConfig.containsKey("domain"))
            return String.valueOf(requestConfig.get("domain"));
        throw new RuntimeException("The domain request or target component name is null");
    }

    private Map<String, String> prepareRequestArguments(Map<String, Object> requestConfig) {
        Map<String, String> resultParams = new HashMap<>();
        List<Map<String, Object>> params = getListOfMaps(requestConfig, "params");
        params.forEach(param -> {
            String name = getFirstKey(param);
            String value = String.valueOf(param.get(name));
            resultParams.put(name, value);
        });
        return resultParams;
    }

    private String checkForNullAndReturn(Map<String, Object> config, String param) {
        if (!config.containsKey(param))
            throw new RuntimeException("The '" + param + "' in jmeter-test-config is null");
        return String.valueOf(config.get(param));
    }

    private String getFirstKey(Map<String, Object> map) {
        return map.keySet()
                .stream()
                .findFirst()
                .orElseThrow();
    }
}
