package app.ezbudget.server.ezbudgetserver.model;

public class Purchase {
    public int id;
    public String name;
    public float amount;
    public String timestamp;

    public Purchase(int id, String name, float amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public Purchase(int id, String name, float amount, String timestamp) {
        this(id, name, amount);
        this.timestamp = timestamp;
    }
}
