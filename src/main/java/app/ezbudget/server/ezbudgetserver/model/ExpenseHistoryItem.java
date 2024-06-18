package app.ezbudget.server.ezbudgetserver.model;

/**
 * Used to send JSON serialized data to the app.
 */
public class ExpenseHistoryItem extends Expense {

    public String month;
    public int year;

    public ExpenseHistoryItem(int id, String name, float amount, String month, int year) {
        super(id, name, amount);
        this.month = month;
        this.year = year;
    }

    public String toString() {
        return "{ id: " + id + ", name: " + name + ", amount: " + amount +
                ", month: " + month + ", year: " + year + " }";
    }
}
