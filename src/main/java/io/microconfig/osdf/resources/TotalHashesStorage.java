package io.microconfig.osdf.resources;

import io.microconfig.osdf.openshift.OCExecutor;

import java.util.List;

import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static io.microconfig.osdf.utils.YamlUtils.createFromString;
import static io.microconfig.osdf.utils.YamlUtils.dump;
import static java.lang.String.join;
import static java.nio.file.Path.of;

public class TotalHashesStorage {
    private final OCExecutor oc;
    private final TotalHashesTable table;

    public TotalHashesStorage(OCExecutor oc) {
        this.oc = oc;
        this.table = getTable(oc);
    }

    public static TotalHashesStorage totalHashesStorage(OCExecutor oc) {
        return new TotalHashesStorage(oc);
    }

    public boolean contains(String component, String hash) {
        String actualHash = table.get(component);
        return hash.equals(actualHash);
    }

    public void setHash(String component, String hash) {
        table.put(component, hash);
    }

    public void save() {
        dump(table, of("/tmp/table"));

        if (tableStorageExists()) {
            String configmap = oc.execute("oc create configmap osdf-total-hashes --from-file /tmp/table --dry-run -o yaml").throwExceptionIfError().getOutput();
            writeStringToFile(of("/tmp/configmap.yaml"), configmap);
            oc.execute("oc replace -f /tmp/configmap.yaml").throwExceptionIfError();
        } else {
            oc.execute("oc create configmap osdf-total-hashes --from-file /tmp/table").throwExceptionIfError();
        }
    }

    private TotalHashesTable getTable(OCExecutor oc) {
        List<String> output = oc.execute("oc get configmap osdf-total-hashes -o custom-columns=\"data:.data.table\"").getOutputLines();
        if (output.get(0).contains("not found")) {
            return new TotalHashesTable();
        }
        String content = join("\n", output.subList(1, output.size()));
        return createFromString(TotalHashesTable.class, content);
    }

    private boolean tableStorageExists() {
        return !oc.execute("oc get configmap osdf-total-hashes").getOutput().toLowerCase().contains("not found");
    }
}
