package unstable.io.osdf.chaos;

import io.osdf.common.exceptions.OSDFException;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.range;

public class ParamsExtractor {
    public static ParamsExtractor paramsExtractor() {
        return new ParamsExtractor();
    }

    @SuppressWarnings("unchecked")
    public List<Integer> intParamToList(Object o, Integer num) {
        if (o instanceof List) {
            List<Integer> paramList = (List<Integer>) o;
            if (paramList.size() == num) {
                return paramList;
            }
            throw new OSDFException("Wrong num of params. Expected: " + num + ", actual: " + paramList.size());
        }
        if (o instanceof Integer) {
            Integer value = (Integer) o;
            return range(0, num).map(i -> value).boxed().collect(Collectors.toUnmodifiableList());
        }
        throw new OSDFException("Can't parse param");
    }

    @SuppressWarnings("unchecked")
    public List<String> strParamToList(Object o, Integer num) {
        if (o instanceof List) {
            List<String> paramList = (List<String>) o;
            if (paramList.size() == num) {
                return paramList;
            }
            throw new OSDFException("Wrong num of params. Expected: " + num + ", actual: " + paramList.size());
        }
        if (o instanceof String) {
            String value = (String) o;
            return range(0, num).mapToObj(i -> value).collect(Collectors.toUnmodifiableList());
        }
        throw new OSDFException("Can't parse param");
    }

    public List<Integer> intParamToListOrEmpty(Object o, Integer stagesNum) {
        if (o == null) return emptyList();
        return intParamToList(o, stagesNum);
    }
}
