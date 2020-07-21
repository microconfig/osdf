package io.osdf.actions.system.install.migrations;

import io.osdf.settings.paths.OsdfPaths;
import io.osdf.settings.version.OsdfVersionFile;
import org.yaml.snakeyaml.error.YAMLException;

import java.nio.file.Path;

import static io.osdf.common.encryption.Encryptor.encryptor;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.utils.YamlUtils.createFromFile;
import static java.nio.file.Files.exists;

public class EncryptionMigration implements Migration {
    public static EncryptionMigration encryptionMigration() {
        return new EncryptionMigration();
    }

    @Override
    public void apply(OsdfPaths paths) {
        if (!encryptionNeeded(paths)) return;

        encryptSettings(paths.settings().gitFetcher());
        encryptSettings(paths.settings().nexusFetcher());
        encryptSettings(paths.settings().localFetcher());
        encryptSettings(paths.settings().openshift());
        encryptSettings(paths.settings().kubernetes());
        encryptSettings(paths.settings().configs());
        encryptSettings(paths.settings().osdf());
        encryptSettings(paths.settings().registryCredentials());
        encryptSettings(paths.settings().clusterContext());
    }

    private void encryptSettings(Path file) {
        if (!exists(file)) return;
        writeStringToFile(file, encryptor.encrypt(readAll(file)));
    }

    private boolean encryptionNeeded(OsdfPaths paths) {
        try {
            createFromFile(OsdfVersionFile.class, paths.settings().osdf());
            return true;
        } catch (YAMLException e) {
            return false;
        }
    }
}
