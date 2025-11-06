package app.ezbudget.server.ezbudgetserver.model;

public class VariableExpense extends Expense {

    public float max;
    public boolean is_account = false;
    public String plaid_item_id;

    public VariableExpense(int id, String name, float amount, float max) {
        super(id, name, amount);
        this.max = max;
    }

    public VariableExpense(int id, String name, float amount, float max, boolean isAccount) {
        super(id, name, amount);
        this.max = max;
        this.is_account = isAccount;
    }

    public VariableExpense(int id, String name, float amount, float max, boolean isAccount, String plaidItemId) {
        super(id, name, amount);
        this.max = max;
        this.is_account = isAccount;
        this.plaid_item_id = plaidItemId;
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

    public String getPlaidItemId() {
        return plaid_item_id;
    }

    public void setPlaidItemId(String item_id) {
        plaid_item_id = item_id;
    }

    @Override
    public String toString() {
        return "{ id: " + this.id + ", name: " + this.name +
                ", amount: " + this.amount + ", max: " + this.max + " }";
    }
}
