package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.exceptions.DailyItemNotFoundException;
import app.ezbudget.server.ezbudgetserver.model.Purchase;
import app.ezbudget.server.ezbudgetserver.model.PurchasedExpense;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.model.VariableExpense;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PurchasesService extends JointService {

    public PurchasesService(DAOFactory factory) {
        super(factory);
    }

    public HTTPResponse getPurchases(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Map<String, PurchasedExpense> items = getDailyPurchases(user);

        return new HTTPResponse(200, "Ok", items);
    }

    public HTTPResponse savePurchases(String authtoken, String key, List<Purchase> items) throws DailyItemNotFoundException {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        float sum = 0;

        for(Purchase p : items)
            sum += p.amount;

        Map<String, PurchasedExpense> daily_items = getDailyPurchases(user);

        try {
            daily_items.get(key).amount = sum;
            daily_items.get(key).setPurchases(items);
        } catch(NullPointerException e) {
            throw new DailyItemNotFoundException("Item \"" + key + "\" was not found.");
        }

        this.factory.getPurchaseDAO().save(user.getAuthtoken(), daily_items);

        return new HTTPResponse(200, "Saved.");
    }

    private Map<String, PurchasedExpense> getDailyPurchases(User user) {
        Map<String, PurchasedExpense> items;

        try {
            items = new HashMap<>(this.factory.getPurchaseDAO().getExpensesWithPurchases(user.getAuthtoken()));

            boolean modified = false;

            //if the items has an extra key that the user's presets doesn't have then remove the key
            if(user.getVariablePresets().size() < items.keySet().size()) {

                Iterator<Map.Entry<String, PurchasedExpense>> iterator = items.entrySet().iterator();

                while(iterator.hasNext()) {

                    String key = iterator.next().getKey();

                    boolean found = false;

                    for (VariableExpense expense : user.getVariablePresets()) {
                        if (expense.getName().equals(key)) {
                            found = true;
                            break;
                        }
                    }

                    if(!found) {
                        iterator.remove();
                        modified = true;
                    }
                }
            }

            //if the items doesn't have a key that the user's preset expenses have then add the key and item
            if(user.getVariablePresets().size() > items.keySet().size()) {
                for (VariableExpense expense : user.getVariablePresets()) {
                    if (!items.containsKey(expense.getName())) {
                        items.put(expense.getName(), new PurchasedExpense(expense));
                        modified = true;
                    }
                }
            }

            //if the variable expenses' max values have changed then update them in the items
            for(VariableExpense expense : user.getVariablePresets()) {
                PurchasedExpense pe = items.get(expense.getName());

                if(pe.getMax() != expense.getMax()) {
                    pe.max = expense.getMax();
                    modified = true;
                }

                if(pe.isAccount() != expense.isAccount()) {
                    pe.is_account = expense.isAccount();
                    modified = true;
                }

                if(pe.getId() != expense.getId()) {
                    pe.id = expense.getId();
                    modified = true;
                }
            }

            if(modified)
                this.factory.getPurchaseDAO().save(user.getAuthtoken(), items);

        } catch (NullPointerException e) {
            items = new HashMap<>();

            for(VariableExpense expense : user.getVariablePresets()) {
                items.put(expense.getName(), new PurchasedExpense(expense));
            }

            this.factory.getPurchaseDAO().save(user.getAuthtoken(), items);
        }

        return items;
    }
}
