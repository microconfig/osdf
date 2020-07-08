package io.osdf.actions.management.deploy.smart.image;

import io.osdf.common.exceptions.OSDFException;
import io.osdf.common.yaml.YamlObject;
import io.osdf.core.application.local.ApplicationFiles;
import io.osdf.core.application.local.metadata.LocalResourceMetadata;
import io.osdf.settings.paths.OsdfPaths;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.osdf.actions.management.deploy.smart.image.ImageDigestGetter.imageDigestGetter;
import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;

@RequiredArgsConstructor
public class ImageTagReplacer {
    private final OsdfPaths paths;

    public static ImageTagReplacer imageTagReplacer(OsdfPaths paths) {
        return new ImageTagReplacer(paths);
    }

    public void replaceFor(ApplicationFiles files) {
        YamlObject deploy = yaml(files.getPath("deploy.yaml"));
        if (!deploy.<Boolean>get("replaceTag.enable")) return;
        boolean continueOnError = deploy.get("replaceTag.continueOnError");

        getDigestAndReplace(files.metadata().getMainResource(), continueOnError);
    }

    private void getDigestAndReplace(LocalResourceMetadata resource, boolean continueOnError) {
        YamlObject resourceYaml = yaml(Path.of(resource.getPath()));
        String imageUrl = imageUrl(resourceYaml);
        if (imageUrl.contains("@")) return;
        String digest = getDigest(imageUrl, continueOnError);
        if (digest == null) return;

        replaceTagInResource(Path.of(resource.getPath()), imageUrl, digest);
    }

    private void replaceTagInResource(Path pathToResource, String imageUrl, String digest) {
        String newImageUrl = imageUrl.split(":")[0] + "@" + digest;
        String newContent = readAll(pathToResource)
                .replace(imageUrl, newImageUrl);
        writeStringToFile(pathToResource, newContent);
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
