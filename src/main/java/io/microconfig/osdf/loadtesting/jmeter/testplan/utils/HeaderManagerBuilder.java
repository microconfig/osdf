package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;

import static org.apache.jmeter.testelement.TestElement.GUI_CLASS;
import static org.apache.jmeter.testelement.TestElement.TEST_CLASS;

public class HeaderManagerBuilder {
    private HeaderManagerBuilder(){}

    public static HeaderManager prepareHeaderManager(String httpRequestName) {
        HeaderManager manager = new HeaderManager();
        manager.add(new Header("content-type", "application/json"));
        manager.setName(httpRequestName + "content-type");
        manager.setProperty(TEST_CLASS, HeaderManager.class.getName());
        manager.setProperty(GUI_CLASS, HeaderPanel.class.getName());
        return manager;
    }
}
