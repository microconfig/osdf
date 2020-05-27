package io.microconfig.osdf.chaos;

import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.microconfig.osdf.utils.YamlUtils.getList;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;

@AllArgsConstructor
public class ChaosTestPlan {
    private final List<ChaosStep> steps;

    public static ChaosTestPlan fromYaml(Path pathToPlan) {
        Map<String, Object> yml = loadFromPath(pathToPlan);
        List<Object> steps = getList(yml, "steps");
        return new ChaosTestPlan(steps.stream().map(ChaosStep::fromMap).collect(Collectors.toUnmodifiableList()));
    }

    public List<ChaosStep> steps() {
        return steps;
    }
}
