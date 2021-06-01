package io.osdf.actions.management.deploy.smart.image;

import io.osdf.actions.management.deploy.smart.image.digest.DigestGetter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import io.osdf.core.application.core.files.metadata.LocalResourceMetadata;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.regex.Matcher;

import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
import static io.osdf.common.yaml.YamlObject.yaml;
import static java.nio.file.Path.of;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNullElse;
import static java.util.regex.Pattern.compile;

@RequiredArgsConstructor
public class ImageVersionReplacer {
    private final DigestGetter digestGetter;

    public static ImageVersionReplacer imageVersionReplacer(DigestGetter digestGetter) {
        return new ImageVersionReplacer(digestGetter);
    }

    public void replaceFor(ApplicationFiles files) {
        if (files.metadata().getMainResource() == null) return;
        String mainResourceContent = readAll(of(files.metadata().getMainResource().getPath()));

        ParsedEntry entry = findEntry(mainResourceContent);
        if (entry == null) return;

        Boolean continueOnError = requireNonNullElse(
                files.deployProperties().get("osdf.replaceVersionWithDigest.continueOnError"), false
        );

        Path resourcePath = of(files.metadata().getMainResource().getPath());
        getDigestAndReplace(resourcePath, entry, continueOnError);
    }

    private void getDigestAndReplace(Path path, ParsedEntry entry, boolean continueOnError) {
        String digest = getDigest(entry.trueUrl(), continueOnError);
        if (digest == null) return;

        String newContent = readAll(path).replace(entry.url, entry.urlWithDigest(digest));
        writeStringToFile(path, newContent);
    }

    private ParsedEntry findEntry(String content) {
        String lineWithImage = stream(content.split("\n"))
                .filter(line -> line.contains("IMAGE_DIGEST"))
                .findFirst()
                .orElse(null);
        if (lineWithImage == null) return null;

        Matcher matcher = compile(".*\\s+(.*(<IMAGE_DIGEST:(.*)>)).*").matcher(lineWithImage);
        if (!matcher.matches()) return null;

        return new ParsedEntry(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    private String getDigest(String imageUrl, boolean continueOnError) {
        try {
            return digestGetter.get(imageUrl);
        } catch (OSDFException e) {
            if (continueOnError) return null;
            throw new OSDFException("Couldn't get digest for " + imageUrl + ": " + e.getMessage());
        }
    }

    @RequiredArgsConstructor
    static class ParsedEntry {
        private final String url;
        private final String versionEntry;
        private final String version;

        public String trueUrl() {
            return url.replace(versionEntry, version);
        }

        public String urlWithDigest(String digest) {
            return url.replace(":" + versionEntry, "@" + digest);
        }
    }

}
