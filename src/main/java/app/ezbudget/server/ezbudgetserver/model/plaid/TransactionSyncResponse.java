package app.ezbudget.server.ezbudgetserver.model.plaid;

import java.util.List;

public class TransactionSyncResponse {
    public class Transaction {
        public String account_id;
        public String transaction_id;
        public String pending_transaction_id;
        public boolean pending;
        public String name;
        public String merchant_name;
        public String authorized_date;
        public String date;
        public Float amount;
    }

    public class RemovedTransaction {
        public String account_id;
        public String transaction_id;
    }

    public String next_cursor;
    public boolean has_more;
    public String request_id;
    public String transactions_update_status;
    public List<Transaction> added;
    public List<Transaction> modified;
    public List<RemovedTransaction> removed;
}
