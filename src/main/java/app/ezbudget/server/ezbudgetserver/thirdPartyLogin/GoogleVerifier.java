package app.ezbudget.server.ezbudgetserver.thirdPartyLogin;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleVerifier implements Verifier<GoogleIdToken> {

    private final String CLIENT_ID = "791758794324-7t0uqbn85iemabqip5l3kq6vfv7kdfa8.apps.googleusercontent.com";

    private GoogleIdTokenVerifier verifier;

    public GoogleVerifier() {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    @Override
    public GoogleIdToken getToken(String credential) throws RuntimeException {
        try {
            return verifier.verify(credential);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
