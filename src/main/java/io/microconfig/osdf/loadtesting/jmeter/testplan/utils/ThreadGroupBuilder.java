package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;

import java.util.Map;

import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ParamChecker.checkForNullAndReturn;
import static org.apache.jmeter.testelement.TestElement.GUI_CLASS;
import static org.apache.jmeter.testelement.TestElement.TEST_CLASS;
import static org.apache.jmeter.threads.AbstractThreadGroup.ON_SAMPLE_ERROR;

public class ThreadGroupBuilder {

    private ThreadGroupBuilder() {}

    public static ThreadGroup prepareThreadGroup(Map<String, Object> userConfig, LoopController loopController) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Jmeter Test Group");
        threadGroup.setNumThreads(Integer.parseInt(checkForNullAndReturn(userConfig, "numberOfThreads")));
        threadGroup.setRampUp(Integer.parseInt(checkForNullAndReturn(userConfig, "rampUpPeriod")));
        threadGroup.setScheduler(true);
        threadGroup.setDuration(Integer.parseInt(checkForNullAndReturn(userConfig, "duration")));
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(new StringProperty(ON_SAMPLE_ERROR, "continue"));
        threadGroup.setProperty(TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(GUI_CLASS, ThreadGroupGui.class.getName());
        return threadGroup;
    }
}
