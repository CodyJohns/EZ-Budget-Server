package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.*;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetService extends JointService {

    private final int NUM_ENTRIES = 12;

    private boolean debugMode = false;

    public BudgetService(DAOFactory factory) {
        super(factory);
    }

    public BudgetService enableDebugMode(boolean enabled) {
        this.debugMode = enabled;
        return this;
    }

    public HTTPResponse getCalculatedExpenses(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<CalculatedExpense> presets = user.getCalculatedPresets();

        return new HTTPResponse(200, "Ok", presets);
    }

    public HTTPResponse getVariableExpenses(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<VariableExpense> presets = user.getVariablePresets();

        return new HTTPResponse(200, "Ok", presets);
    }

    public HTTPResponse getVariableExpensesWithTotalPurchasesAmount(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<PurchasedExpense> presets = new ArrayList<>();

        presets.addAll(this.factory.getPurchaseDAO().getExpensesWithPurchases(user.getAuthtoken()).values());

        for (PurchasedExpense preset : presets) {

            if (preset.is_account) {
                preset.amount = 0;
            } else {
                float sum = 0;

                for (Purchase purchase : preset.getPurchases()) {
                    sum += purchase.amount;
                }

                preset.amount = sum;
            }
        }

        return new HTTPResponse(200, "Ok", presets);
    }

    public HTTPResponse updateCalculatedExpenses(String authtoken, List<CalculatedExpense> presets) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        user.setCalculatedPresets(presets);

        this.factory.getUserDAO().save(user);

        return new HTTPResponse(200, "Updated");
    }

    @Deprecated
    /**
     * Will be removed since this contains a bug.
     */
    public HTTPResponse updateVariableExpenses(String authtoken, List<VariableExpense> presets) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        user.setVariablePresets(presets);

        this.factory.getUserDAO().save(user);

        return new HTTPResponse(200, "Updated");
    }

    public HTTPResponse updateVariableExpensesV2(String authtoken, List<VariableExpense> presets, List<NameEdit> nameEdits) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Map<String, PurchasedExpense> purchases = this.getExistingOrNewExpensesWithPurchases(user);

        for (NameEdit edit : nameEdits) {
            String old_key = edit.old_exp.name;
            String new_key = edit.new_exp.name;

            PurchasedExpense purchasedExpense = purchases.get(old_key);

            if (purchasedExpense == null)
                continue;

            purchasedExpense.name = new_key;

            //just copy the value for the old key to the new one
            purchases.put(new_key, purchasedExpense);
            //the getDailyPurchases will finish removing the unused keys
        }

        if (nameEdits.size() > 0)
            this.factory.getPurchaseDAO().save(user.getAuthtoken(), purchases);

        user.setVariablePresets(presets);

        this.factory.getUserDAO().save(user);

        if (debugMode)
            return new HTTPResponse(200, "Updated", purchases);

        return new HTTPResponse(200, "Updated");
    }

    private Map<String, PurchasedExpense> getExistingOrNewExpensesWithPurchases(User user) {
        try {
            return new HashMap<>(this.factory.getPurchaseDAO().getExpensesWithPurchases(user.getAuthtoken()));
        } catch(NullPointerException e) {
            return new HashMap<>();
        }
    }

    public HTTPResponse getBudgetOverview(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<Entry> recentEntries = this.factory.getEntryDAO().getSpecificNumberOfEntries(user.getAuthtoken(), NUM_ENTRIES);

        BudgetOverview data = new BudgetOverview(user.getCalculatedPresets(), user.getVariablePresets());
        data.calculateAverageMonthlyIncome(recentEntries);

        return new HTTPResponse(200, "Ok", data);
    }
}
