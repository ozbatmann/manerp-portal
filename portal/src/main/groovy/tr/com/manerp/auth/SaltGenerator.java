package tr.com.manerp.auth;

import java.security.SecureRandom;

public class SaltGenerator {

    private static final int MAX_SALT_SIZE = 16;

    public static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[MAX_SALT_SIZE];
        sr.nextBytes(salt);
        return salt;
    }
}
