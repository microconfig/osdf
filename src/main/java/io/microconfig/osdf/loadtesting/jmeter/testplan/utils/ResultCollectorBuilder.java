package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.SummaryReport;

public class ResultCollectorBuilder {
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
        logger.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
        logger.setProperty(TestElement.GUI_CLASS, SummaryReport.class.getName());
        return logger;
    }
}
