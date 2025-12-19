package app.ezbudget.server.ezbudgetserver.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;

@Service
public class PlaidKeyService {
    private final ConcurrentMap<String, JWK> cache = new ConcurrentHashMap<>();
    private String clientId;
    private String secret;
    private String plaidUrl;
    private Gson gson;

    class PlaidJWTKeyResponse {
        class KeyObject {
            public String alg;
            public String crv;
            public String kid;
            public String kty;
            public String use;
            public String x;
            public String y;
            public Integer created_at;
            public Integer expired_at;
        }

        public KeyObject key;
        public String request_id;
    }

    public PlaidKeyService() {
        clientId = System.getenv("PLAID_CLIENT_ID");
        secret = System.getenv("PLAID_SECRET");
        plaidUrl = System.getenv("PLAID_URL");
        gson = new Gson();
    }

    public JWK getJwkForKid(String kid) throws ParseException {
        JWK cached = cache.get(kid);
        if (cached != null)
            return cached;

        String json = gson.toJson(Map.of(
                "client_id", clientId,
                "secret", secret,
                "key_id", kid));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(plaidUrl + "/webhook_verification_key/get");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);

                JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonObject keyNode = root.getAsJsonObject("key");
                if (keyNode == null)
                    throw new IllegalStateException("Missing key");

                JWK jwk = JWK.parse(keyNode.toString());
                cache.put(kid, jwk);
                return jwk;
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
