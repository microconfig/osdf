package unstable.io.osdf.chaos.validators;

import unstable.io.osdf.chaos.types.Chaos;
import unstable.io.osdf.chaos.types.ChaosType;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class BasicValidator {
    private final Set<List<Chaos>> chaosSet;

    public static BasicValidator basicValidator(Set<List<Chaos>> chaosSet) {
        return new BasicValidator(chaosSet);
    }

    public void basicCheck() {
        chaosSet.parallelStream().forEach(list -> list.forEach(Chaos::check));
    }

    public void checkNetworkChaosIntersections() {
        checkIntersection(ChaosType.NETWORK);
    }

    public void checkPodChaosIntersections() {
        checkIntersection(ChaosType.POD);
    }

    //just check the first list items, because the rest have the same component lists
    private void checkIntersection(ChaosType type) {
        Set<Chaos> filteredSet = chaosSet.stream()
                .map(chaosList -> chaosList.get(0))
                .filter(chaos -> chaos.type().equals(type))
                .collect(toUnmodifiableSet());

        Set<String> components = new HashSet<>();
        filteredSet.forEach(chaos -> {
            if (!Collections.disjoint(components, chaos.getComponents())) {
                throw new OSDFException("Components in " + type + " chaos should not intersect");
            }
            components.addAll(chaos.getComponents());
        });
    }
}
