package unstable.io.osdf.chaos.validators;

import unstable.io.osdf.chaos.types.Chaos;
import io.osdf.common.exceptions.OSDFException;
import lombok.RequiredArgsConstructor;
import unstable.io.osdf.chaos.types.ChaosType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Collections.disjoint;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RequiredArgsConstructor
public class PodAndIOChaosIntersectionValidator {
    private final Set<List<Chaos>> chaosSet;

    public static PodAndIOChaosIntersectionValidator podAndIOChaosIntersectionValidator(Set<List<Chaos>> chaosSet) {
        return new PodAndIOChaosIntersectionValidator(chaosSet);
    }

    //just check the first list items, because the rest have the same component lists
    public void podAndIOChaosIntersectionCheck() {
        Set<String> ioComponents = chaosSet.stream()
                .map(chaosList -> chaosList.get(0))
                .filter(chaos -> chaos.type().equals(ChaosType.IO))
                .map(Chaos::getComponents)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet());

        Set<String> podComponents = chaosSet.stream()
                .map(chaosList -> chaosList.get(0))
                .filter(chaos -> chaos.type().equals(ChaosType.POD))
                .map(Chaos::getComponents)
                .flatMap(Collection::stream)
                .collect(toUnmodifiableSet());

        if (!disjoint(ioComponents, podComponents)) {
            throw new OSDFException("Components in io and pod chaos should not intersect");
        }
    }
}
