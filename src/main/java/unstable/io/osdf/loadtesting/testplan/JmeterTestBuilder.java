package unstable.io.osdf.loadtesting.testplan;

import io.osdf.common.exceptions.PossibleBugException;
import lombok.RequiredArgsConstructor;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import unstable.io.osdf.loadtesting.testplan.utils.LoopControllerBuilder;
import unstable.io.osdf.loadtesting.testplan.utils.SimpleRequestBuilder;
import unstable.io.osdf.loadtesting.testplan.utils.ThreadGroupBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.osdf.common.utils.FileUtils.*;
import static io.osdf.common.utils.YamlUtils.loadFromPath;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class JmeterTestBuilder {
    private final Path jmeterComponentsPath;
    private final Map<String, String> componentsRoutes;

    public static JmeterTestBuilder jmeterConfigBuilder(Path jmeterComponentsPath, Map<String, String> componentsRoutes) {
        return new JmeterTestBuilder(jmeterComponentsPath, componentsRoutes);
    }

    public Path build() {
        Path testTemplatePath = of(jmeterComponentsPath + "/config/jmetertest.jmx");
        Map<String, Object> userConfig = loadFromPath(of(jmeterComponentsPath + "/application.yaml"));
        createDirectoryIfNotExists(of(jmeterComponentsPath + "/config"));
        initJmeterEngineConfigs();
        prepareTestPlan(userConfig, testTemplatePath);
        return testTemplatePath;
    }

    private void prepareTestPlan(Map<String, Object> userConfig, Path testTemplatePath) {
        LoopController loopController = LoopControllerBuilder.prepareLoopController();
        ThreadGroup threadGroup = ThreadGroupBuilder.prepareThreadGroup(userConfig, loopController);

        HashTree httpSamplerHashTree = prepareHttpSamplerHashTree(userConfig);
        HashTree testPlanTree = prepareTestPlanTree(threadGroup, httpSamplerHashTree);
        saveFile(testTemplatePath, testPlanTree);
    }

    private HashTree prepareTestPlanTree(ThreadGroup threadGroup, HashTree httpSamplerHashTree) {
        TestPlan testPlan = prepareTestPlan();
        HashTree testPlanTree = prepareTestPlanTree(testPlan);
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(httpSamplerHashTree);
        return testPlanTree;
    }

    private HashTree prepareTestPlanTree(TestPlan testPlan) {
        HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);
        return testPlanTree;
    }

    private HashTree prepareHttpSamplerHashTree(Map<String, Object> userConfig) {
        HashTree httpSamplerHashTree = new ListedHashTree();
        Map<String, HeaderManager> headerManagerMap = new HashMap<>();
        List<HTTPSamplerProxy> httpSamplers = SimpleRequestBuilder.simpleRequestBuilder(componentsRoutes).requests(userConfig, headerManagerMap);
        httpSamplers.forEach(httpSampler -> {
            HeaderManager manager = headerManagerMap.get(httpSampler.getName());
            httpSamplerHashTree.add(httpSampler, manager);
        });
        return httpSamplerHashTree;
    }

    private TestPlan prepareTestPlan() {
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        return testPlan;
    }

    private void saveFile(Path testTemplatePath, HashTree testPlanTree) {
        try {
            SaveService.saveTree(testPlanTree, new FileOutputStream(testTemplatePath.toString()));
        } catch (IOException e) {
            throw new PossibleBugException("Error in saving jmetertest.jmx", e);
        }
    }

    private void initJmeterEngineConfigs() {
        Path jmeterConfigsPath = of(jmeterComponentsPath + "/config/jmeter/bin");
        Path jmeterPropertiesPath = of(jmeterConfigsPath + "/jmeter.properties");
        createDirectoriesIfNotExists(jmeterConfigsPath);
        createFileIfNotExists(of(jmeterConfigsPath + "/saveservice.properties"));
        createFileIfNotExists(jmeterPropertiesPath);
        writeStringToFile(of(jmeterConfigsPath + "/saveservice.properties"),
                getContentFromResource(of("/jmeter/bin/saveservice.properties")));
        writeStringToFile(jmeterPropertiesPath, getContentFromResource(of("/jmeter/bin/jmeter.properties")));
        JMeterUtils.setJMeterHome(of(jmeterComponentsPath + "/config/jmeter").toString());
        JMeterUtils.loadJMeterProperties(jmeterPropertiesPath.toString());
    }
}