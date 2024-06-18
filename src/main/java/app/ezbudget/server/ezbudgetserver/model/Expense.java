package app.ezbudget.server.ezbudgetserver.model;

/**
 * Base class or generic class for an expense object.
 */
public class Expense {

    public int id;
    public String name;
    public float amount;

    public Expense(int id, String name, float amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }
}
