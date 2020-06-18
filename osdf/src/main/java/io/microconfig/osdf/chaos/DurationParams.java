package io.microconfig.osdf.chaos;

import io.microconfig.osdf.exceptions.OSDFException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.getString;
import static java.time.Duration.parse;

@RequiredArgsConstructor
public class DurationParams {
    private final Duration stageDuration;
    @Getter
    private final Integer stagesNum;

    public static DurationParams fromYaml(Map<String, Object> yml) {
        String durationStr = getString(yml, "duration");
        Integer stagesNum = getInt(yml, "stages");

        if (durationStr == null) throw new OSDFException("Duration is missing");
        if (stagesNum == null) throw new OSDFException("Number of stages is missing");

        String isoDurationString = convertToIso8601(durationStr);
        Duration duration = parse(isoDurationString);
        Duration stageDuration = duration.dividedBy(stagesNum);
        return new DurationParams(stageDuration, stagesNum);
    }

    private static String convertToIso8601(String durationStr) {
        return "PT" + durationStr.toUpperCase();
    }

    public Long getStageDurationInSec() {
        return stageDuration.toSeconds();
    }

    public Long getStageDurationInMillis() {
        return stageDuration.toMillis();
    }
}
