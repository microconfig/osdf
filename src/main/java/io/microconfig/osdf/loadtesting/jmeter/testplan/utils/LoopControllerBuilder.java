package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;

import static org.apache.jmeter.testelement.TestElement.GUI_CLASS;
import static org.apache.jmeter.testelement.TestElement.TEST_CLASS;

public class LoopControllerBuilder {
    private LoopControllerBuilder(){}

    public static LoopController prepareLoopController() {
        LoopController loopController = new LoopController();
        loopController.setContinueForever(false);
        loopController.setLoops(-1);
        loopController.setFirst(true);
        loopController.setProperty(TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        return loopController;
    }
}
