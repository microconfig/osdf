package io.microconfig.osdf.cluster.resource.totalhash;

import io.microconfig.osdf.cluster.cli.ClusterCLI;
import io.microconfig.osdf.resources.TotalHashesTable;

import java.util.List;

import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static io.microconfig.osdf.utils.YamlUtils.createFromString;
import static io.microconfig.osdf.utils.YamlUtils.dump;
import static java.lang.String.join;
import static java.nio.file.Path.of;

public class TotalHashesStorage {
    private final ClusterCLI cli;
    private final TotalHashesTable table;

    public TotalHashesStorage(ClusterCLI cli) {
        this.cli = cli;
        this.table = getTable(cli);
    }

    public static TotalHashesStorage totalHashesStorage(ClusterCLI cli) {
        return new TotalHashesStorage(cli);
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
            String configmap = cli.execute("create configmap osdf-total-hashes --from-file /tmp/table --dry-run -o yaml").throwExceptionIfError().getOutput();
            writeStringToFile(of("/tmp/configmap.yaml"), configmap);
            cli.execute("replace -f /tmp/configmap.yaml").throwExceptionIfError();
        } else {
            cli.execute("create configmap osdf-total-hashes --from-file /tmp/table").throwExceptionIfError();
        }
    }

    private TotalHashesTable getTable(ClusterCLI cli) {
        List<String> output = cli.execute("get configmap osdf-total-hashes -o custom-columns=\"data:.data.table\"").getOutputLines();
        if (output.get(0).contains("not found")) {
            return new TotalHashesTable();
        }
        String content = join("\n", output.subList(1, output.size()));
        return createFromString(TotalHashesTable.class, content);
    }

    private boolean tableStorageExists() {
        return !cli.execute("get configmap osdf-total-hashes").getOutput().toLowerCase().contains("not found");
    }
}
