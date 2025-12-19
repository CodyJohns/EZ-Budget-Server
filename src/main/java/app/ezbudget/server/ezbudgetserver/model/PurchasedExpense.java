package app.ezbudget.server.ezbudgetserver.model;

import java.util.ArrayList;
import java.util.List;

public class PurchasedExpense extends VariableExpense {

    private List<Purchase> items;

    public PurchasedExpense(int id, String name, float amount, float max) {
        super(id, name, amount, max);
        items = new ArrayList<>();
    }

    public PurchasedExpense(int id, String name, float amount, float max, List<Purchase> purchases) {
        super(id, name, amount, max);
        this.items = purchases;
    }

    public PurchasedExpense(VariableExpense expense) {
        super(expense.getId(), expense.getName(), expense.getAmount(), expense.getMax(), expense.isAccount(),
                expense.getPlaidItemId());
        items = new ArrayList<>();
    }

    public List<Purchase> getPurchases() {
        return items;
    }

    public void setPurchases(List<Purchase> items) {
        this.items = items;
    }
}
