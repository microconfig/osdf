package io.osdf.actions.management.deploy.smart.image;

import io.osdf.actions.management.deploy.smart.image.digest.DigestGetter;
import io.osdf.common.exceptions.OSDFException;
import io.osdf.core.application.core.files.ApplicationFiles;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;

import static io.osdf.common.utils.FileUtils.readAll;
import static io.osdf.common.utils.FileUtils.writeStringToFile;
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
        Boolean continueOnError = requireNonNullElse(
                files.deployProperties().get("osdf.replaceVersionWithDigest.continueOnError"), false
        );

        files.resources().forEach(resource -> {
            String newContent = replaceEntries(readAll(resource.path()), continueOnError);
            writeStringToFile(resource.path(), newContent);
        });
    }

    private String replaceEntries(String content, boolean continueOnError) {
        while (true) {
            ParsedEntry entry = findEntry(content);
            if (entry == null) return content;

            String digest = getDigest(entry.trueUrl(), continueOnError);
            content = content.replace(entry.url, digest == null ? entry.trueUrl() : entry.urlWithDigest(digest));
        }
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
