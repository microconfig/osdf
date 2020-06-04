package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import java.util.Map;

public class ParamChecker {
    public static String checkForNullAndReturn(Map<String, Object> config, String param) {
        if (!config.containsKey(param))
            throw new RuntimeException("The '" + param + "' in jmeter-test-config is null");
        return String.valueOf(config.get(param));
    }
}
