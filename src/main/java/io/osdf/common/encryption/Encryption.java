package io.osdf.common.encryption;

import org.jasypt.util.text.StrongTextEncryptor;
import org.jasypt.util.text.TextEncryptor;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class Encryption {
    public static final TextEncryptor encryptor = getEncryptor();

    private static TextEncryptor getEncryptor() {
        StrongTextEncryptor encryptor = new StrongTextEncryptor();
        encryptor.setPassword(md5Hex("1026566"));
        return encryptor;
    }
}
