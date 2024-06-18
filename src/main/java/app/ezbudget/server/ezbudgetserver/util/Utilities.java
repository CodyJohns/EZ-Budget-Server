package app.ezbudget.server.ezbudgetserver.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class Utilities {

    private static PasswordEncoder encoder;

    /**
     * Generate a hashed, random string.
     * 
     * @param length
     * @return
     */
    public static String generateAuthtoken(int length) {
        return md5(generateString(length));
    }

    /**
     * Get password encoder for user login and registration.
     * 
     * @return PasswordEncoder
     */
    public static PasswordEncoder getEncoder() {
        if(encoder == null)
            encoder = new BCryptPasswordEncoder();

        return encoder;
    }

    /**
     * Generate an unhashed, random string.
     * 
     * @param length
     * @return Unhashed random string
     */
    public static String generateCode(int length) {
        return generateString(length);
    }

    private static String generateString(int size) {
        String alpha = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        int len = alpha.length();

        Random rand = new Random();
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < size; i++) {
            builder.append(alpha.charAt(rand.nextInt(0, len)));
        }

        return builder.toString();
    }

    private static String md5(String raw) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] message = md.digest(raw.getBytes(StandardCharsets.UTF_8));

            BigInteger integ = new BigInteger(1, message);

            String hash = integ.toString(16);

            while(hash.length() < 32) {
                hash = "0" + hash;
            }

            return hash;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Change timestamp string to unix long type. String format should be: e.g. "May 2024"
     * 
     * @param timestamp
     * @return Long
     * @throws ParseException
     */
    public static Long timestampToUnix(String timestamp) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");

        return sdf.parse(timestamp).getTime() / 1000;
    }
}
