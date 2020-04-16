package io.microconfig.osdf.install;

import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.microconfig.properties.OSDFDownloadProperties;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.state.OSDFState;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.microconfig.properties.OSDFDownloadProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.nexus.NexusArtifact.nexusArtifact;
import static io.microconfig.osdf.nexus.NexusClient.nexusClient;
import static io.microconfig.osdf.state.OSDFState.createState;
import static io.microconfig.osdf.state.OSDFState.fromFile;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.FileUtils.readAll;
import static io.microconfig.osdf.utils.FileUtils.writeStringToFile;
import static io.microconfig.osdf.utils.JarUtils.jarPath;
import static io.microconfig.utils.Logger.warn;
import static java.lang.System.getProperty;
import static java.nio.file.Files.exists;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;
import static org.apache.commons.io.FileUtils.getUserDirectoryPath;

@RequiredArgsConstructor
public class OSDFInstaller {
    private static final String SCRIPT_NAME = "osdf";

    private final OSDFPaths paths;

    public static OSDFInstaller osdfInstaller(OSDFPaths paths) {
        return new OSDFInstaller(paths);
    }

    public void install(OSDFVersion oldVersion, OSDFVersion newVersion, OSDFSource source) {
        if (oldVersion.hasOlderMinorThan(newVersion)) {
            warn("Current version is significantly older than version in configs");
            warn("You'll need to init osdf again after update. Old state will be saved at " + paths.oldStateSavePath());
            osdfInstaller(paths).install(oldVersion, newVersion, source, true);
        } else {
            osdfInstaller(paths).install(oldVersion, newVersion, source, false);
        }
    }

    public void install(OSDFVersion oldVersion, OSDFVersion newVersion, OSDFSource source, boolean clearState) {
        if (source == null) {
            createState(newVersion, paths.stateSavePath());
            return;
        }

        newJar(newVersion, source);

        Path scriptPath = of(paths.scriptFolder() + "/" + SCRIPT_NAME);
        Path tmpScriptPath = of(paths.scriptFolder() + "/_" + SCRIPT_NAME);

        createNewScript(tmpScriptPath, newVersion);
        if (clearState) {
            replaceJarAndCreateState(scriptPath, tmpScriptPath, newVersion, oldVersion != null);
        } else {
            replaceJarAndUseOldState(scriptPath, tmpScriptPath, newVersion);
        }
        addScriptToBashrc();
        if (!newVersion.equals(oldVersion)) deleteOldJar(oldVersion);
    }

    private void newJar(OSDFVersion newVersion, OSDFSource source) {
        switch (source) {
            case REMOTE:
                downloadJar(newVersion);
                break;
            case LOCAL:
                copyJar(newVersion);
                break;
            default:
                throw new RuntimeException("Unknown jar source");
        }
    }

    private void createNewScript(Path tmpScriptPath, OSDFVersion version) {
        Path pathToJava = get(getProperty("java.home").replace(" ", "\\ "), "bin", "java");
        String content =
                "if [ $# -gt 0  ] && [ $1 == \"logs\" ]\n" +
                        "then\n" +
                        "        trap '' SIGINT\n" +
                        "fi\n" +
                        pathToJava + " -XX:TieredStopAtLevel=1 -jar " + paths.root() + "/" + jarName(version) + " ${@:1}";
        writeStringToFile(tmpScriptPath, content);
        execute("chmod +x " + tmpScriptPath);
    }

    private void replaceJarAndUseOldState(Path scriptPath, Path tmpScriptPath, OSDFVersion newVersion) {
        OSDFState state = fromFile(paths.stateSavePath());
        state.setOsdfVersion(newVersion.toString());
        state.save(paths.newStateSavePath());
        execute("mv " + tmpScriptPath + " " + scriptPath);
        execute("mv " + paths.newStateSavePath() + " " + paths.stateSavePath());
    }

    private void replaceJarAndCreateState(Path scriptPath, Path tmpScriptPath, OSDFVersion newVersion, boolean saveOldState) {
        if (saveOldState) execute("mv " + paths.stateSavePath() + " " + paths.oldStateSavePath());
        createState(newVersion, paths.stateSavePath());
        execute("mv " + tmpScriptPath + " " + scriptPath);
    }

    private void addScriptToBashrc() {
        String newEntry = "PATH=$PATH:" + paths.scriptFolder() + "/";

        Path bashrc = of(getUserDirectoryPath() + "/.bashrc");
        if (!exists(bashrc)) {
            writeStringToFile(bashrc, newEntry);
            return;
        }

        String bashrcContent = readAll(bashrc);
        if (!bashrcContent.contains(newEntry)) {
            writeStringToFile(bashrc, bashrcContent + "\n" + newEntry + "\n");
        }
    }

    private void deleteOldJar(OSDFVersion oldVersion) {
        execute("rm -rf " + paths.root() + "/" + jarName(oldVersion));
    }

    private void downloadJar(OSDFVersion version) {
        OSDFState state = fromFile(paths.stateSavePath());
        OSDFDownloadProperties downloadProperties = properties(propertyGetter(state.getEnv(), paths.configPath()));

        String url = state.getNexusUrl();
        if (url == null) {
            warn("Nexus url wasn't provided. Will use url from configs");
            url = downloadProperties.url();
        }
        NexusArtifact nexusArtifact = nexusArtifact(downloadProperties.group(), downloadProperties.artifact(), version.toString(), "jar");
        nexusClient(url, state.getNexusCredentials()).download(nexusArtifact, of(paths.root() + "/" + jarName(version)));
    }

    private void copyJar(OSDFVersion version) {
        execute("cp " + jarPath() + " " + paths.root() + "/osdf-" + version + ".jar");
    }

    private String jarName(OSDFVersion version) {
        return "osdf-" + version + ".jar";
    }
}
