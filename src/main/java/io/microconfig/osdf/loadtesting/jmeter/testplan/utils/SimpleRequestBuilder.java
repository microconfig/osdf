package io.microconfig.osdf.loadtesting.jmeter.testplan.utils;

import io.microconfig.osdf.exceptions.PossibleBugException;
import lombok.RequiredArgsConstructor;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.DomainBuilder.domainBuilder;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.HeaderManagerBuilder.prepareHeaderManager;
import static io.microconfig.osdf.loadtesting.jmeter.testplan.utils.ParamUtils.*;
import static io.microconfig.osdf.utils.YamlUtils.getListOfMaps;
import static io.microconfig.osdf.utils.YamlUtils.getMap;
import static org.apache.jmeter.testelement.TestElement.GUI_CLASS;
import static org.apache.jmeter.testelement.TestElement.TEST_CLASS;

@RequiredArgsConstructor
public class SimpleRequestBuilder {
    private final Map<String, String> componentsRoutes;

    public static SimpleRequestBuilder simpleRequestBuilder(Map<String, String> componentsRoutes) {
        return new SimpleRequestBuilder(componentsRoutes);
    }

    public List<HTTPSamplerProxy> requests(Map<String, Object> userConfig, Map<String, HeaderManager> headerManager) {
        Map<String, Object> userPlan = getMap(userConfig, "plan");
        if (userPlan == null) throw new PossibleBugException("Plan not found. Please add 'plan' field in your config");
        return userPlan.keySet()
                .stream()
                .map(key -> getMap(userPlan, key))
                .map(plan -> getListOfMaps(plan, "requests"))
                .flatMap(List::stream)
                .map(request -> prepareSimpleRequest(request, headerManager))
                .collect(Collectors.toList());
    }

    private HTTPSamplerProxy prepareSimpleRequest(Map<String, Object> request, Map<String, HeaderManager> headerManager) {
        String httpRequestName = getFirstKey(request);
        Map<String, Object> requestConfig = getMap(request, httpRequestName);

        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setDomain(domainBuilder(componentsRoutes).prepareDomain(requestConfig));
        httpSampler.setName(httpRequestName);
        httpSampler.setMethod(checkForNullAndReturn(requestConfig, "method"));
        httpSampler.setPath(addPath(requestConfig));
        httpSampler.setProtocol(checkForNullAndReturn(requestConfig, "protocol"));
        httpSampler.setUseKeepAlive(true);
        httpSampler.setProperty(TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(GUI_CLASS, HttpTestSampleGui.class.getName());
        addParams(requestConfig, httpSampler);
        addBody(requestConfig, httpSampler);
        addHeaderManager(headerManager, httpRequestName, requestConfig);
        return httpSampler;
    }

    private String addPath(Map<String, Object> requestConfig) {
        String path = checkForNullAndReturn(requestConfig, "path");
        return addPathParamsIfExists(path, requestConfig);
    }

    private void addHeaderManager(Map<String, HeaderManager> headerManagerMap, String httpRequestName,
                                  Map<String, Object> requestConfig) {
        HeaderManager headerManager = prepareHeaderManager(httpRequestName);
        headerManagerMap.put(httpRequestName, headerManager);
        if (requestConfig.containsKey("headers")) {
            getListOfMaps(requestConfig, "headers").forEach(header -> {
                String name = getFirstKey(header);
                headerManager.add(new Header(name, String.valueOf(header.get(name))));
            });
            headerManagerMap.put(httpRequestName, headerManager);
        }
    }

    private void addParams(Map<String, Object> requestConfig, HTTPSamplerProxy httpSampler) {
        String method = checkForNullAndReturn(requestConfig, "method");
        if (method.equals("GET") && requestConfig.containsKey("params")) {
            Map<String, String> params = prepareRequestParams(requestConfig);
            params.forEach((key, value) -> httpSampler.addArgument(key, value, "="));
        }
    }

    private void addBody(Map<String, Object> requestConfig, HTTPSamplerProxy httpSampler) {
        String method = checkForNullAndReturn(requestConfig, "method");
        if (!method.equals("GET")) {
            String body = checkForNullAndReturn(requestConfig, "body");
            httpSampler.setPostBodyRaw(true);
            httpSampler.addNonEncodedArgument("", body, "=");
        }
    }

    private String addPathParamsIfExists(String path, Map<String, Object> requestConfig) {
        String method = checkForNullAndReturn(requestConfig, "method");
        StringJoiner pathWithParams = new StringJoiner("&", path + "?", "");
        if (!method.equals("GET") && requestConfig.containsKey("params")) {
            prepareRequestParams(requestConfig).forEach((key, value) -> pathWithParams.add(key + "=" + value));
            return pathWithParams.toString();
        }
        return path;
    }
}
