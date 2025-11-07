package app.ezbudget.server.ezbudgetserver.model;

public class Purchase {
    public int id;
    public String name;
    public float amount;
    public String timestamp;
    public String transaction_id;
    public String pending_transaction_id;

    public Purchase(int id, String name, float amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public Purchase(int id, String name, float amount, String timestamp) {
        this(id, name, amount);
        this.timestamp = timestamp;
    }

    public Purchase(int id, String name, float amount, String timestamp, String transactionId,
            String pendingTransactionId) {
        this(id, name, amount, timestamp);
        this.transaction_id = transactionId;
        this.pending_transaction_id = pendingTransactionId;
    }
}
