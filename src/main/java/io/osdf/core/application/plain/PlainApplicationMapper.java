package io.osdf.core.application.plain;

import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.loaders.ApplicationMapper;
import io.osdf.core.connection.cli.ClusterCli;
import lombok.RequiredArgsConstructor;

import static io.osdf.core.application.plain.PlainApplication.plainApplication;

@RequiredArgsConstructor
public class PlainApplicationMapper implements ApplicationMapper<PlainApplication> {
    private final ClusterCli cli;

    public static PlainApplicationMapper plain(ClusterCli cli) {
        return new PlainApplicationMapper(cli);
    }

    @Override
    public boolean check(ApplicationFiles files) {
        return files.metadata().getType().equals("PLAIN");
    }

    @Override
    public PlainApplication map(ApplicationFiles files) {
        return plainApplication(files, cli);
    }
}
