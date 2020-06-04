package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.testelement.TestElement;

public class HeaderManagerBuilder {
    public static HeaderManager prepareHeaderManager(String httpRequestName) {
        HeaderManager manager = new HeaderManager();
        manager.add(new Header("content-type", "application/json"));
        manager.setName(httpRequestName + "content-type");
        manager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        manager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        return manager;
    }
}
