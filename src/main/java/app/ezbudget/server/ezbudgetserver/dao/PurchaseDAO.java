package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.model.PurchasedExpense;

import java.util.Map;

public interface PurchaseDAO {

    /**
     * Get a user's expenses. This returns a map of expenses with a sub list of purchases for each expense.
     * 
     * @param authtoken
     * @return Map<String, PurchasedExpense> expenses
     */
    Map<String, PurchasedExpense> getExpensesWithPurchases(String authtoken);

    /**
     * Save or update a user's expenses.
     * 
     * @param authtoken
     * @param items
     */
    void save(String authtoken, Map<String, PurchasedExpense> items);

    /**
     * Delete a user's expenses. This should only be called in rare cases.
     * 
     * @param authtoken
     */
    void delete(String authtoken);
}
