package app.ezbudget.server.ezbudgetserver.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

public class PlaidService extends JointService {

    class PlaidLinkCreateResponse {
        public String link_token;
        public String expiration;
        public String request_id;
    }

    public PlaidService(DAOFactory factory) {
        super(factory);
    }

    public HTTPResponse<PlaidLinkCreateResponse> getLinkToken(String authtoken) {
        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        String clientId = System.getenv("PLAID_CLIENT_ID");
        String secret = System.getenv("PLAID_SECRET");

        if (clientId == null || secret == null) {
            throw new RuntimeException("Missing PLAID_CLIENT_ID and/or PLAID_SECRET env variables.");
        }

        Gson gson = new Gson();

        String json = gson.toJson(Map.of(
            "client_id", clientId, 
            "secret", secret,
            "client_name", "EZ-Budget",
            "language", "en",
            "country_codes", List.of("US"),
            "user", Map.of("client_user_id", user.getAuthtoken()),
            "products", List.of("transactions")
        ));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("https://sandbox.plaid.com/link/token/create");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response Body: " + responseBody);

                return new HTTPResponse<PlaidLinkCreateResponse>(200, "Success", gson.fromJson(responseBody, PlaidLinkCreateResponse.class));
            }
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
