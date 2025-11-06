package app.ezbudget.server.ezbudgetserver.model.plaid;

public class PlaidItem {

    public enum ItemStatus {
        Active,
        Error,
    }

    public String item_id; // key
    public String access_token;
    public String cursor;
    public ItemStatus state;
    public String state_desc;
    public String authtoken;
    public Integer expenseId;
}
