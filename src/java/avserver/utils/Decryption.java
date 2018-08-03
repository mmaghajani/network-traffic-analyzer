/** * In The Name of Allah ** */
package avserver.utils;

import avserver.config.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Utility class for data decryption.
 */
public class Decryption {

    /**
     * Decrypts a given crypto parameter (such as secret-key, or IV) using the private RSA-key of the given user.
     */
    public static byte[] decryptParam(byte[] param, String username)
            throws IOException, GeneralSecurityException {
        Path keyPath = Paths.get(Config.KEYS_DIR + username + ".der");
        byte[] privateKeyBytes = Files.readAllBytes(keyPath);
        PKCS8EncodedKeySpec prvSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key privateKey = keyFactory.generatePrivate(prvSpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(param);
    }

    /**
     * Decrypts a given file using AES in CBC mode with the given key and IV.
     */
    public static void decryptFile(String filePath, byte[] key, byte[] iv/*, byte[] tag*/)
            throws GeneralSecurityException, IOException, DecoderException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        Path file = Paths.get(filePath);
		byte[] cipheredData = Hex.decodeHex(new String(Files.readAllBytes(file)).toCharArray());
        byte[] plainData = cipher.doFinal(cipheredData);
        Files.write(file, Base64.getDecoder().decode(plainData));

    }

}
