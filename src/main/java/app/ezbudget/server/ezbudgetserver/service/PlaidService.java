package app.ezbudget.server.ezbudgetserver.service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.api.client.util.DateTime;
import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.Purchase;
import app.ezbudget.server.ezbudgetserver.model.PurchasedExpense;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.model.plaid.PlaidItem;
import app.ezbudget.server.ezbudgetserver.model.plaid.PlaidTransactionUpdate;
import app.ezbudget.server.ezbudgetserver.model.plaid.TransactionSyncResponse;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

public class PlaidService extends JointService {

    class PlaidLinkCreateResponse {
        public String link_token;
        public String expiration;
        public String request_id;
    }

    class PlaidItemRemoveResponse {
        public boolean removed;
        public String request_id;
    }

    private Gson gson;
    private String clientId;
    private String secret;
    private String plaidWebhookUrl;
    private String plaidUrl;

    public PlaidService(DAOFactory factory) {
        super(factory);
        gson = new Gson();
        clientId = System.getenv("PLAID_CLIENT_ID");
        secret = System.getenv("PLAID_SECRET");
        plaidWebhookUrl = System.getenv("PLAID_WEBHOOK_URL");
        plaidUrl = System.getenv("PLAID_URL");
    }

    public HTTPResponse<PlaidLinkCreateResponse> getLinkToken(String authtoken) {
        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        if (clientId == null || secret == null) {
            throw new RuntimeException("Missing PLAID_CLIENT_ID and/or PLAID_SECRET env variables.");
        }

        String json = gson.toJson(Map.of(
                "client_id", clientId,
                "secret", secret,
                "client_name", "EZ-Budget",
                "language", "en",
                "country_codes", List.of("US"),
                "user", Map.of("client_user_id", user.getAuthtoken()),
                "products", List.of("transactions"),
                "webhook", plaidWebhookUrl));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(plaidUrl + "/link/token/create");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                return new HTTPResponse<PlaidLinkCreateResponse>(200, "Success",
                        gson.fromJson(responseBody, PlaidLinkCreateResponse.class));
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

    public HTTPResponse<String> savePublicToken(String authtoken, String public_token) {
        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        if (clientId == null || secret == null) {
            throw new RuntimeException("Missing PLAID_CLIENT_ID and/or PLAID_SECRET env variables.");
        }

        String json = gson.toJson(Map.of(
                "client_id", clientId,
                "secret", secret,
                "public_token", public_token));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(plaidUrl + "/item/public_token/exchange");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                PlaidItem item = gson.fromJson(responseBody, PlaidItem.class);
                item.state = PlaidItem.ItemStatus.Active;
                item.authtoken = authtoken;

                if (user.items == null) {
                    user.items = new ArrayList<>();
                }

                user.getPlaidAccessTokens().add(item);

                this.factory.getTransactionDAO().saveItem(item);
                this.factory.getUserDAO().save(user);

                return new HTTPResponse<String>(200, "Success", gson.toJson(Map.of("id", item.item_id), Map.class));
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

    public boolean removeItem(PlaidItem item) {
        if (clientId == null || secret == null) {
            throw new RuntimeException("Missing PLAID_CLIENT_ID and/or PLAID_SECRET env variables.");
        }

        String json = gson.toJson(Map.of(
                "client_id", clientId,
                "secret", secret,
                "access_token", item.access_token));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(plaidUrl + "/item/remove");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                PlaidItemRemoveResponse responseObj = gson.fromJson(responseBody, PlaidItemRemoveResponse.class);

                return responseObj.removed;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public TransactionSyncResponse transactionsSync(PlaidItem item) {
        if (clientId == null || secret == null) {
            throw new RuntimeException("Missing PLAID_CLIENT_ID and/or PLAID_SECRET env variables.");
        }

        Map<String, String> payload = Map.of(
                "client_id", clientId,
                "secret", secret,
                "access_token", item.access_token);

        if (item.cursor != null) {
            payload.put("cursor", item.cursor);
        }

        String json = gson.toJson(payload);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(plaidUrl + "/transactions/sync");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                return gson.fromJson(responseBody, TransactionSyncResponse.class);
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

    private void processTransactions(TransactionSyncResponse response, PurchasedExpense expense) {
        Set<String> removedIds = response.removed.stream()
                .map(item -> item.transaction_id)
                .collect(Collectors.toSet());
        expense.setPurchases(expense.getPurchases().stream()
                .filter(exp -> removedIds.contains(exp.transaction_id))
                .collect(Collectors.toList()));

        List<Purchase> new_purchases = new ArrayList<>();
        Integer size = expense.getPurchases().size();
        for (TransactionSyncResponse.Transaction transaction : response.added) {
            if (!transaction.pending) {
                String[] dateArr = transaction.date.split("-");
                new_purchases.add(
                        new Purchase(
                                size + new_purchases.size(),
                                transaction.merchant_name,
                                transaction.amount,
                                dateArr[1] + "/" + dateArr[2],
                                transaction.transaction_id));
            }
        }
        expense.getPurchases().addAll(new_purchases);
        expense.amount = expense.getPurchases().stream()
                .mapToInt(p -> (int) Math.round(p.amount * 100))
                .sum() / 100;
    }

    private void updateUserPurchases(String itemId) {
        PlaidItem item = this.factory.getTransactionDAO().getItem(itemId);
        Map<String, PurchasedExpense> expenses = this.factory.getPurchaseDAO()
                .getExpensesWithPurchases(item.authtoken);
        PurchasedExpense expense = expenses.values()
                .stream()
                .filter(exp -> item.item_id.equals(exp.plaid_item_id)).findFirst().orElse(null);

        if (expense == null)
            throw new RuntimeException("Cannot find PurchaseExpense object using item_id");

        TransactionSyncResponse response = transactionsSync(item);
        item.cursor = response.next_cursor;
        processTransactions(response, expense);

        boolean hasMore = response.has_more;

        while (hasMore) {
            response = transactionsSync(item);
            item.cursor = response.next_cursor;
            processTransactions(response, expense);
            hasMore = response.has_more;
        }

        this.factory.getTransactionDAO().saveItem(item);
        this.factory.getPurchaseDAO().save(item.authtoken, expenses);
    }

    public HTTPResponse<String> processUpdate(PlaidTransactionUpdate update) {
        if (update.webhook_type.equals("TRANSACTIONS")) {
            if (update.webhook_code.equals("SYNC_UPDATES_AVAILABLE")) {
                updateUserPurchases(update.item_id);
            }
        } else if (update.webhook_type.equals("ITEM")) {
            PlaidItem item = this.factory.getTransactionDAO().getItem(update.item_id);
            boolean modified = false;

            if (update.webhook_code.equals("ERROR")) {
                item.state = PlaidItem.ItemStatus.Error;
                item.state_desc = update.error.error_message;
                modified = true;
            } else if (update.webhook_code.equals("LOGIN_REPAIRED")) {
                item.state = PlaidItem.ItemStatus.Active;
                item.state_desc = null;
                modified = true;
            } else if (update.webhook_code.equals("PENDING_DISCONNECT")) {
                item.state = PlaidItem.ItemStatus.Active;
                item.state_desc = update.reason;
                modified = true;
            } else if (update.webhook_code.equals("PENDING_EXPIRATION")) {
                item.state = PlaidItem.ItemStatus.Active;
                Instant instant = Instant.parse(update.consent_expiration_time);
                ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mma", Locale.ENGLISH);
                String formatted = zdt.format(formatter).replace("AM", "am").replace("PM", "pm");
                item.state_desc = "Expires at " + formatted + ". Re-add this item to cancel expiration.";
                modified = true;
            } else if (update.webhook_code.equals("USER_PERMISSION_REVOKED")) {
                item.state = PlaidItem.ItemStatus.Error;
                item.state_desc = update.error.error_message;
                modified = true;
            } else if (update.webhook_code.equals("USER_ACCOUNT_REVOKED")) {
                item.state = PlaidItem.ItemStatus.Error;
                item.state_desc = "Account holder has revoked access to this item.";
                modified = true;
            }

            if (modified) {
                this.factory.getTransactionDAO().saveItem(item);
            }
        }

        return new HTTPResponse<String>(200, "Ok");
    }
}
