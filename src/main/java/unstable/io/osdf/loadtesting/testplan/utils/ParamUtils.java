package unstable.io.osdf.loadtesting.testplan.utils;

import io.osdf.common.exceptions.OSDFException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.osdf.common.utils.YamlUtils.getListOfMaps;

public class ParamUtils {
    private ParamUtils(){}

    public static String checkForNullAndReturn(Map<String, Object> config, String param) {
        if (!config.containsKey(param))
            throw new OSDFException("The '" + param + "' in jmeter-test-config is null");
        return String.valueOf(config.get(param));
    }

    public static Map<String, String> prepareRequestParams(Map<String, Object> requestConfig) {
        Map<String, String> resultParams = new HashMap<>();
        List<Map<String, Object>> params = getListOfMaps(requestConfig, "params");
        params.forEach(param -> {
            String name = getFirstKey(param);
            resultParams.put(name, String.valueOf(param.get(name)));
        });
        return resultParams;
    }

    public static String getFirstKey(Map<String, Object> map) {
        return map.keySet()
                .stream()
                .findFirst()
                .orElseThrow();
    }
}
