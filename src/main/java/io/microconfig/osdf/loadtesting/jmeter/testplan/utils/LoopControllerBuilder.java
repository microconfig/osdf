package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.testelement.TestElement;

public class LoopControllerBuilder {
    public static LoopController prepareLoopController() {
        LoopController loopController = new LoopController();
        loopController.setContinueForever(false);
        loopController.setLoops(-1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        return loopController;
    }
}
