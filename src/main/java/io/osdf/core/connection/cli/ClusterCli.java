package io.osdf.core.connection.cli;

public interface ClusterCli {
    CliOutput execute(String command);

    void login();

    void logout();
}
