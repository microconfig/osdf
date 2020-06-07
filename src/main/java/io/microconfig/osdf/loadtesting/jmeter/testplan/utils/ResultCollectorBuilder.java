package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.SummaryReport;

import static org.apache.jmeter.testelement.TestElement.GUI_CLASS;
import static org.apache.jmeter.testelement.TestElement.TEST_CLASS;

public class ResultCollectorBuilder {
    private ResultCollectorBuilder() {}

    public static ResultCollector prepareResultCollector() {
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }

        String logFile = "/tmp/summary-result.jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        logger.setName("Summary Report");
        logger.setProperty(TEST_CLASS, ResultCollector.class.getName());
        logger.setProperty(GUI_CLASS, SummaryReport.class.getName());
        return logger;
    }
}
