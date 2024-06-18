package app.ezbudget.server.ezbudgetserver.model;

public class VariableExpense extends Expense {

    public float max;
    public boolean is_account = false;

    public VariableExpense(int id, String name, float amount, float max) {
        super(id, name, amount);
        this.max = max;
    }

    public VariableExpense(int id, String name, float amount, float max, boolean isAccount) {
        super(id, name, amount);
        this.max = max;
        this.is_account = isAccount;
    }

    public float getMax() {
        return max;
    }

    /**
     * Check if expense is trackable by user.
     * 
     * @return True if expense is trackable on Daily Items
     */
    public boolean isAccount() {
        return is_account;
    }

    @Override
    public String toString() {
        return "{ id: " + this.id + ", name: " + this.name +
                ", amount: " + this.amount + ", max: " + this.max + " }";
    }
}
