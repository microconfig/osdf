package io.osdf.core.connection.cli;

public interface ClusterCli {
    CliOutput execute(String command);

    CliOutput execute(String command, int timeoutInSec);

    void login();

    void logout();
}
