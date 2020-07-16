package io.osdf.test.cluster;

import io.osdf.core.connection.cli.CliOutput;
import io.osdf.core.connection.cli.ClusterCli;

import static io.osdf.core.connection.cli.CliOutput.errorOutput;

public class TestCli implements ClusterCli, ExtendableApi {
    @Override
    public CliOutput execute(String command) {
        return errorOutput("unknown command", 1);
    }

    @Override
    public CliOutput execute(String command, int timeoutInSec) {
        return execute(command);
    }

    @Override
    public void login() {
        //no login for test cli
    }

    @Override
    public void logout() {
        //no logout for test cli
    }
}
