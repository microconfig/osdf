package io.osdf.actions.chaos.checks;

import io.osdf.actions.chaos.ChaosContext;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Map.of;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class CheckersLoader {
    private final ChaosContext chaosContext;

    public static CheckersLoader checkersLoader(ChaosContext chaosContext) {
        return new CheckersLoader(chaosContext);
    }

    public List<Checker> load(Map<String, Object> description) {
        return checkers().entrySet().stream()
                .filter(entry -> description.containsKey(entry.getKey()))
                .map(entry -> entry.getValue().apply(description.get(entry.getKey()), chaosContext))
                .collect(toUnmodifiableList());
    }

    private Map<String, BiFunction<Object, ChaosContext, Checker>> checkers() {
        return of(
                "liveness", LivenessChecker::livenessChecker,
                "http", HttpChecker::httpChecker
        );
    }
}
