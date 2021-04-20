package io.osdf.actions.chaos.state;

import io.osdf.actions.chaos.assaults.ActiveAssaultInfo;
import io.osdf.common.exceptions.PossibleBugException;
import io.osdf.common.utils.FileUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static io.osdf.common.utils.FileUtils.createDirectoryIfNotExists;
import static io.osdf.common.utils.YamlUtils.createFromFile;
import static io.osdf.common.utils.YamlUtils.dump;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;

@RequiredArgsConstructor
public class AssaultInfoManager {
    private final Path assaultInfoDir;

    public static AssaultInfoManager assaultInfoManager(Path assaultInfoDir) {
        return new AssaultInfoManager(assaultInfoDir);
    }

    public void save(ActiveAssaultInfo info) {
        createDirectoryIfNotExists(assaultInfoDir);
        dump(info, assaultInfoFilename(info.getAssaultName()));
    }

    public ActiveAssaultInfo get(String name) {
        Path path = assaultInfoFilename(name);
        if (!exists(path)) return null;
        return createFromFile(ActiveAssaultInfo.class, path);
    }

    public void delete(String name) {
        FileUtils.delete(assaultInfoFilename(name));
    }

    public List<ActiveAssaultInfo> list() {
        if (!exists(assaultInfoDir)) return emptyList();

        try (Stream<Path> paths = Files.list(assaultInfoDir)) {
            return paths.map(file -> createFromFile(ActiveAssaultInfo.class, file))
                    .collect(toUnmodifiableList());
        } catch (IOException e) {
            throw new PossibleBugException("Can't list assaults at " + assaultInfoDir, e);
        }
    }

    private Path assaultInfoFilename(String assaultName) {
        return of(assaultInfoDir + "/" + assaultName + ".yaml");
    }
}
