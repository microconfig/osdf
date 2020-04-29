package io.microconfig.osdf.install.migrations;

import io.microconfig.osdf.paths.OSDFPaths;

import java.util.Map;

import static io.microconfig.osdf.utils.YamlUtils.*;
import static java.nio.file.Files.exists;
import static java.util.Map.of;

public class AddTokenMigration implements Migration {
    @Override
    public void apply(OSDFPaths paths) {
        if (!exists(paths.newStateSavePath())) return;
        Map<String, Object> stateObject = loadFromPath(paths.newStateSavePath());
        if (notAnOldVersion(stateObject)) {
            return;
        }

        fix(stateObject);
        dump(stateObject, paths.newStateSavePath());
    }

    private void fix(Map<String, Object> stateObject) {
        Map<String, Object> credentials = getMap(stateObject, "openShiftCredentials");
        Map<String, Map<String, Object>> newCredentials = of("credentials", credentials);
        stateObject.put("openShiftCredentials", newCredentials);
    }

    private boolean notAnOldVersion(Map<String, Object> stateObject) {
        return getObjectOrNull(stateObject, "openShiftCredentials", "credentialsString") == null;
    }
}
