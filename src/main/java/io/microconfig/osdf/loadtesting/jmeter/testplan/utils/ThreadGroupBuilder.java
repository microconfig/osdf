package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;

import java.util.Map;

import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ParamChecker.checkForNullAndReturn;

public class ThreadGroupBuilder {
    public static ThreadGroup prepareThreadGroup(Map<String, Object> userConfig, LoopController loopController) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Jmeter Test Group");
        threadGroup.setNumThreads(Integer.parseInt(checkForNullAndReturn(userConfig, "numberOfThreads")));
        threadGroup.setRampUp(Integer.parseInt(checkForNullAndReturn(userConfig, "rampUpPeriod")));
        threadGroup.setScheduler(true);
        threadGroup.setDuration(Integer.parseInt(checkForNullAndReturn(userConfig, "duration")));
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(new StringProperty(ThreadGroup.ON_SAMPLE_ERROR, "continue"));
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        return threadGroup;
    }
}
