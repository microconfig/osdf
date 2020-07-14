package io.osdf.actions.management.deploy.smart.image;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.metadata.LocalResourceMetadata;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;

import static io.osdf.actions.management.deploy.smart.image.ImageDigestGetter.imageDigestGetter;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Path.of;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNullElse;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class ImageTagReplacer {
    private final OsdfPaths paths;

    public static ImageTagReplacer imageTagReplacer(OsdfPaths paths) {
        return new ImageTagReplacer(paths);
    }

    public void replaceFor(ApplicationFiles files) {
        String mainResourceContent = readAll(of(files.metadata().getMainResource().getPath()));

        String versionEntry = findVersionEntry(mainResourceContent);
        if (versionEntry == null) return;

        String version = findVersion(versionEntry);
        if (version == null) throw new OSDFException("Bad image version format: " + versionEntry);

        Boolean continueOnError = yaml(files.getPath("deploy.yaml"))
                .get("osdf.replaceVersionWithDigest.continueOnError");

        getDigestAndReplace(files.metadata().getMainResource(), versionEntry, version,
                requireNonNullElse(continueOnError, false));
    }

    private void getDigestAndReplace(LocalResourceMetadata resource, String versionEntry, String version,
                                     boolean continueOnError) {
        YamlObject resourceYaml = yaml(of(resource.getPath()));
        String imageUrl = imageUrl(resourceYaml);
        if (imageUrl.contains("@")) return;

        String digest = getDigest(imageUrl.replace(versionEntry, version), continueOnError);
        if (digest == null) return;

        replaceTagInResource(of(resource.getPath()), imageUrl, imageUrl.replace(":" + versionEntry, "@" + digest));
    }

    private void replaceTagInResource(Path pathToResource, String imageUrl, String newImageUrl) {
        String newContent = readAll(pathToResource)
                .replace(imageUrl, newImageUrl);
        writeStringToFile(pathToResource, newContent);
    }

    private String findVersionEntry(String mainResourceContent) {
        String lineWithImage = stream(mainResourceContent.split("\n"))
                .filter(line -> line.contains("IMAGE_DIGEST"))
                .findFirst()
                .orElse(null);
        if (lineWithImage == null) return null;

        Matcher matcher = compile(".*(<IMAGE_DIGEST:.*>).*").matcher(lineWithImage);
        if (!matcher.matches()) return null;
        return matcher.group(1);
    }

    private String findVersion(String versionEntry) {
        Matcher matcher = compile("<IMAGE_DIGEST:(.*)>").matcher(versionEntry);
        if (!matcher.matches()) return null;
        return matcher.group(1);
    }

    private String imageUrl(YamlObject resourceYaml) {
        return resourceYaml
                .<List<Object>>get("spec.template.spec.containers")
                .stream()
                .map(YamlObject::yaml)
                .map(container -> container.<String>get("image"))
                .findFirst()
                .orElseThrow(() -> new OSDFException("Container not found"));
    }

    private String getDigest(String imageUrl, boolean continueOnError) {
        try {
            return imageDigestGetter(imageUrl, paths).get();
        } catch (OSDFException e) {
            if (continueOnError) return null;
            throw new OSDFException("Couldn't get digest for " + imageUrl + ": " + e.getMessage());
        }
    }
}
