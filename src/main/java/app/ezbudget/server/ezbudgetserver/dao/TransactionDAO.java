package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.model.plaid.PlaidItem;

public interface TransactionDAO {
    /**
     * Create a Plaid item which is linked to an account the user selected.
     * 
     * @param item
     */
    void saveItem(PlaidItem item);

    /**
     * Get a plaid item by its item_id.
     * 
     * @param item_id
     * @return PlaidItem
     */
    PlaidItem getItem(String item_id);

    /**
     * Delete the item from our records.
     * 
     * @param item
     */
    void deleteItem(PlaidItem item);

    // void saveTransaction();
    // void deleteTransaction();
}
