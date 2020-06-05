package io.microconfig.osdf.loadtesting.jmeter.testplan;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
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

import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.LoopControllerBuilder.prepareLoopController;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ResultCollectorBuilder.prepareResultCollector;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.SimpleRequestBuilder.simpleRequestBuilder;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ThreadGroupBuilder.prepareThreadGroup;
import static io.microconfig.osdf.utils.YamlUtils.getListOfMaps;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class JmeterTestBuilder {
    private final Path jmeterComponentsPath;
    private final Map<String, String> componentsRoutes;

    public static JmeterTestBuilder jmeterConfigBuilder(Path jmeterComponentsPath, Map<String, String> componentsRoutes) {
        return new JmeterTestBuilder(jmeterComponentsPath, componentsRoutes);
    }

    public Path build(Path userTestConfigPath) {
        Path testTemplatePath = of(jmeterComponentsPath + "/config/jmetertest.jmx");
        Map<String, Object> userConfig = loadFromPath(userTestConfigPath);
        prepareTestPlan(userConfig, testTemplatePath);
        return testTemplatePath;
    }

    private void prepareTestPlan(Map<String, Object> userConfig, Path testTemplatePath) {
        initJmeterEngineConfigs();

        //Prepare plan components
        List<Map<String, Object>> requests = getListOfMaps(userConfig, "requests");
        LoopController loopController = prepareLoopController();
        ThreadGroup threadGroup = prepareThreadGroup(userConfig, loopController);
        Map<String, HeaderManager> headerManagerMap = new HashMap<>();
        List<HTTPSamplerProxy> httpSamplers = simpleRequestBuilder(componentsRoutes)
                .prepareRequests(requests, headerManagerMap);
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        //Add TestPlan and ThreadGroup
        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);

        //Add SimpleRequests
        HashTree httpSamplerHashTree = new ListedHashTree();
        for (HTTPSamplerProxy httpSampler : httpSamplers) {
            httpSamplerHashTree.add(httpSampler, headerManagerMap.get(httpSampler.getName()));
        }
        threadGroupHashTree.add(httpSamplerHashTree);

        //Add ResultCollector
        ResultCollector logger = prepareResultCollector();
        threadGroupHashTree.add(logger);

        //SaveFile
        try {
            SaveService.saveTree(testPlanTree, new FileOutputStream(testTemplatePath.toString()));
        } catch (IOException e) {
            throw new OSDFException("Error in saving jmetertest.jmx", e);
        }
    }

    private void initJmeterEngineConfigs() {
        String jmeterResourcesPath = Objects.requireNonNull(getClass().getClassLoader().getResource("jmeter")).getPath();
        JMeterUtils.setJMeterHome(jmeterResourcesPath);
        JMeterUtils.loadJMeterProperties(jmeterResourcesPath + "/bin/jmeter.properties");
    }
}