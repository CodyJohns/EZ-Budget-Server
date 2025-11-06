package app.ezbudget.server.ezbudgetserver.model.plaid;

public class PlaidTransactionUpdate {
    public class Error {
        public String display_message;
        public String error_code;
        public String error_code_reason;
        public String error_message;
        public String error_type;
        public Integer status;
    }

    public String webhook_type;
    public String webhook_code;
    public String item_id;
    public boolean initial_update_complete;
    public boolean historical_update_complete;
    public String environment;
    public Error error;
    public String reason;
    public String consent_expiration_time;
}
