package io.osdf.api.lib.definitions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.microconfig.utils.ConsoleColor.green;
import static io.microconfig.utils.Logger.info;
import static io.osdf.common.utils.StringUtils.pad;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@Getter
@RequiredArgsConstructor
public class ApiEntrypointDefinition {
    private final List<String> publicApiGroups;
    private final Map<String, ApiGroupDefinition> apiGroupDefinitions;

    public void printUsage() {
        info(
                publicApiGroups.stream()
                        .map(apiGroupDefinitions::get)
                        .map(this::apiGroupUsage)
                        .filter(not(String::isEmpty))
                        .collect(joining("\n\n"))
        );
    }

    public List<String> commands() {
        return apiGroupDefinitions.values().stream()
                .map(ApiGroupDefinition::getApiDefinition)
                .map(ApiDefinition::getPublicMethods)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableList());
    }

    private String apiGroupUsage(ApiGroupDefinition apiGroup) {
        ApiDefinition api = apiGroup.getApiDefinition();
        if (api.getPublicMethods().isEmpty()) return "";
        return green(apiGroup.getName()) + "\n" + apiUsage(api);
    }

    private String apiUsage(ApiDefinition api) {
        return api
                .getPublicMethods().stream()
                .map(methodName -> api.getMethods().get(methodName))
                .map(methodDefinition -> " " + pad(methodDefinition.getMethod().getName(), 20) + methodDefinition.usageHelp())
                .collect(joining("\n"));
    }
}
