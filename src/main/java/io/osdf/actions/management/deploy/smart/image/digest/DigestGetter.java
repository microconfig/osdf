package io.osdf.actions.management.deploy.smart.image.digest;

public interface DigestGetter {
    String get(String imageUrl);
}
