package io.osdf.common.encryption;

import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import static java.lang.System.getenv;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class Encryption {
    public static final TextEncryptor encryptor = getEncryptor();

    private static TextEncryptor getEncryptor() {
        StrongTextEncryptor encryptor = new StrongTextEncryptor();
        encryptor.setPassword(getPassword());
        return encryptor;
    }

    private static String getPassword() {
        String keyFromEnv = getenv("OSDF_ENCRYPTION_KEY");
        if (keyFromEnv != null && !keyFromEnv.trim().isEmpty()) return keyFromEnv;

        return md5Hex("1026566");
    }
}
