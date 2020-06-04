package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import lombok.RequiredArgsConstructor;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.HeaderManagerBuilder.prepareHeaderManager;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ParamChecker.checkForNullAndReturn;
import static io.microconfig.osdf.utils.YamlUtils.getListOfMaps;
import static io.microconfig.osdf.utils.YamlUtils.getMap;

@RequiredArgsConstructor
public class SimpleRequestBuilder {

    private final Map<String, String> componentsRoutes;

    public static SimpleRequestBuilder simpleRequestBuilder(Map<String, String> componentsRoutes) {
        return new SimpleRequestBuilder(componentsRoutes);
    }

    public List<HTTPSamplerProxy> prepareRequests(List<Map<String, Object>> requests,
                                                  Map<String, HeaderManager> headerManagerMap) {
        return requests.stream()
                .map(request -> prepareSimpleRequest(request, headerManagerMap))
                .collect(Collectors.toList());
    }

    private HTTPSamplerProxy prepareSimpleRequest(Map<String, Object> request,
                                                  Map<String, HeaderManager> headerManagerMap) {
        String httpRequestName = getFirstKey(request);
        Map<String, Object> requestConfig = getMap(request, httpRequestName);

        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        if (requestConfig.containsKey("params")) {
            Map<String, String> params = prepareRequestArguments(requestConfig);
            params.forEach((key, value) -> httpSampler.addArgument(key, value, "="));
        }
        String method = checkForNullAndReturn(requestConfig, "method");
        if (!method.equals("GET")) {
            String body = checkForNullAndReturn(requestConfig, "body");
            httpSampler.setPostBodyRaw(true);
            httpSampler.addNonEncodedArgument("Body Data", body, "=");
        }
        httpSampler.setDomain(prepareDomain(requestConfig));
        httpSampler.setName(httpRequestName);
        httpSampler.setMethod(method);
        httpSampler.setPath(checkForNullAndReturn(requestConfig, "path"));
        httpSampler.setProtocol(checkForNullAndReturn(requestConfig, "protocol"));

        httpSampler.setUseKeepAlive(true);
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        HeaderManager headerManager = prepareHeaderManager(httpRequestName);
        headerManagerMap.put(httpRequestName, headerManager);
        if (requestConfig.containsKey("headers")) {
            getListOfMaps(requestConfig, "headers").forEach(header -> {
                String name = getFirstKey(header);
                String value = String.valueOf(header.get(name));
                headerManager.add(new Header(name, value));
            });
            headerManagerMap.put(httpRequestName, headerManager);
        }
        return httpSampler;
    }

    private String prepareDomain(Map<String, Object> requestConfig) {
        if (requestConfig.containsKey("component"))
            return componentsRoutes.get(String.valueOf(requestConfig.get("component")));
        if (requestConfig.containsKey("domain"))
            return String.valueOf(requestConfig.get("domain"));
        throw new RuntimeException("The domain request or target component name is null");
    }

    private Map<String, String> prepareRequestArguments(Map<String, Object> requestConfig) {
        Map<String, String> resultParams = new HashMap<>();
        List<Map<String, Object>> params = getListOfMaps(requestConfig, "params");
        params.forEach(param -> {
            String name = getFirstKey(param);
            String value = String.valueOf(param.get(name));
            resultParams.put(name, value);
        });
        return resultParams;
    }

    private String getFirstKey(Map<String, Object> map) {
        return map.keySet()
                .stream()
                .findFirst()
                .orElseThrow();
    }
}
