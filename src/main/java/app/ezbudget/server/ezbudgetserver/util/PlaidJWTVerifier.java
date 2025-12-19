package app.ezbudget.server.ezbudgetserver.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class PlaidJWTVerifier {

    private final PlaidKeyService keyService;

    public PlaidJWTVerifier(PlaidKeyService keyService) {
        this.keyService = keyService;
    }

    public boolean verify(String jwt, String rawBody) throws ParseException, JOSEException {
        if (jwt == null || jwt.isBlank())
            return false;

        // Parse the JWT
        SignedJWT signed = SignedJWT.parse(jwt);
        JWSHeader hdr = signed.getHeader();

        // alg must be ES256
        if (!JWSAlgorithm.ES256.equals(hdr.getAlgorithm())) {
            return false;
        }
        String kid = hdr.getKeyID();
        if (kid == null)
            return false;

        // Get (and cache) the JWK from Plaid
        JWK jwk = keyService.getJwkForKid(kid);
        if (jwk == null || !(jwk instanceof ECKey)) {
            return false;
        }
        ECPublicKey publicKey = ((ECKey) jwk).toECPublicKey();

        // Verify signature
        boolean sigOk = signed.verify(new ECDSAVerifier(publicKey));
        if (!sigOk)
            return false;

        // Verify iat within 5 minutes
        JWTClaimsSet claims = signed.getJWTClaimsSet();
        Date iat = claims.getIssueTime(); // Nimbus maps "iat"
        if (iat == null)
            return false;

        // Verify body hash
        String claimedHashHex = (String) claims.getClaim("request_body_sha256");
        if (claimedHashHex == null)
            return false;
        String bodyHashHex = sha256Hex(rawBody.getBytes(StandardCharsets.UTF_8));
        // Constant-time comparison
        if (!constantTimeEqualsHex(claimedHashHex, bodyHashHex)) {
            return false;
        }

        return true;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean constantTimeEqualsHex(String a, String b) {
        byte[] aa = a.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        byte[] bb = b.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(aa, bb);
    }
}
