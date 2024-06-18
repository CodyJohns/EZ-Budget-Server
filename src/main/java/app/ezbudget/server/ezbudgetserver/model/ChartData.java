package app.ezbudget.server.ezbudgetserver.model;

/**
 * Used when sending back JSON serialized data for app.
 */
public class ChartData {

    public String month;
    public float amount;

    public ChartData(String month, float amount) {
        this.month = month;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "{ month: " + month + ", amount: " + amount + " }";
    }
}
