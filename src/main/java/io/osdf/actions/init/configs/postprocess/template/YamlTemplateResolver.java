package io.osdf.actions.init.configs.postprocess.template;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class YamlTemplateResolver {
    private static final Pattern SET = compile("~set\\(?([^\\)]*)?\\)?");
    private static final Pattern INCLUDE = compile("~include\\(?([^\\)]*)?\\)?");
    private static final Pattern ADD = compile("~add\\(([^\\)]*)\\)");
    private static final Pattern IF = compile(".*~if\\(?([^\\)]*)?\\)?");
    private final YamlObject keySource;

    public static YamlTemplateResolver yamlTemplateResolver(YamlObject keySource) {
        return new YamlTemplateResolver(keySource);
    }

    public Map<String, Object> resolve(Map<String, Object> yaml) {
        return resolveMap(yaml, 0);
    }

    private Map<String, Object> resolveMap(Map<String, Object> map, int depth) {
        Map<String, Object> newMap = new HashMap<>();
        for (String key : map.keySet()) {
            String actualKey = ifKey(key);
            if (actualKey == null) continue;

            Object valueObj = map.get(key);
            if (!(valueObj instanceof String)) {
                newMap.put(actualKey, valueObj);
                continue;
            }

            String value = (String) valueObj;
            if (includeKey(newMap, actualKey, value)) continue;
            if (setKey(newMap, actualKey, value)) continue;
            newMap.put(key, valueObj);
        }
        newMap.replaceAll((k, v) -> resolveObject(v, depth + 1));
        return new HashMap<>(newMap);
    }

    private boolean setKey(Map<String, Object> newMap, String key, String value) {
        Matcher matcher = SET.matcher(value);
        if (!matcher.find()) return false;

        String customKey = matcher.group(1);
        Object ref = keySource.get(customKey.isEmpty() ? key : customKey);
        if (ref == null) return true;

        newMap.put(key, ref);
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean includeKey(Map<String, Object> newMap, String key, String value) {
        Matcher matcher = INCLUDE.matcher(value);
        if (!matcher.find()) return false;

        String mapKey = matcher.group(1).isEmpty() ? key : matcher.group(1);
        Object objToInclude = keySource.get(mapKey);
        if (objToInclude == null) return true;
        if (!(objToInclude instanceof Map)) throw new OSDFException("Can't ~include non-map object " + mapKey);
        Map<String, Object> mapToInclude = (Map<String, Object>) objToInclude;
        newMap.putAll(mapToInclude);
        return true;
    }

    private String ifKey(String key) {
        Matcher matcher = IF.matcher(key);
        if (matcher.find()) {
            String conditionKey = matcher.group(1);
            Object condition = keySource.get(conditionKey.replaceFirst("!", ""));
            boolean invert = conditionKey.startsWith("!");
            boolean isFalseValue = condition == null || condition.toString().equals("false");
            if (isFalseValue && !invert || !isFalseValue && invert) return null;

            return key.substring(0, key.indexOf("~"));
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    private List<Object> resolveList(List<Object> list, int depth) {
        List<Object> resolvedList = new ArrayList<>();
        for (Object entry : list) {
            boolean isString = entry instanceof String;
            if (!isString) {
                resolvedList.add(resolveObject(entry, depth + 1));
                continue;
            }
            String strEntry = (String) entry;
            Matcher matcher = ADD.matcher(strEntry);
            if (!matcher.find()) {
                resolvedList.add(resolveObject(entry, depth + 1));
                continue;
            }
            Object objToAdd = keySource.get(matcher.group(1));
            if (objToAdd == null) continue;
            if (objToAdd instanceof List) {
                ((List<Object>) objToAdd)
                        .forEach(subEntry ->
                                resolvedList.add(resolveObject(subEntry, depth + 1))
                        );
            } else {
                resolvedList.add(resolveObject(objToAdd, depth + 1));
            }
        }
        return resolvedList;
    }

    @SuppressWarnings("unchecked")
    private Object resolveObject(Object obj, int depth) {
        if (depth > 30) throw new OSDFException("Template error: reached maximum yaml depth");
        if (obj == null) return null;

        if (obj instanceof List) return resolveList((List<Object>) obj, depth);
        if (obj instanceof Map) return resolveMap((Map<String, Object>) obj, depth);
        return obj;
    }
}
