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

        List<VariableExpense> presets = new ArrayList<>();

        presets.addAll(this.factory.getPurchaseDAO().getExpensesWithPurchases(user.getAuthtoken()).values());

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

        //TODO: get the user's purchases to update the key that corresponds to the purchaseexpense with the name edit
        Map<String, PurchasedExpense> purchases = new HashMap<>(this.factory.getPurchaseDAO().getExpensesWithPurchases(user.getAuthtoken()));

        for (NameEdit edit : nameEdits) {
            String old_key = edit.old_exp.name;
            String new_key = edit.new_exp.name;

            PurchasedExpense purchasedExpense = purchases.get(old_key);

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

    public HTTPResponse getBudgetOverview(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<Entry> recentEntries = this.factory.getEntryDAO().getSpecificNumberOfEntries(user.getAuthtoken(), NUM_ENTRIES);

        BudgetOverview data = new BudgetOverview(user.getCalculatedPresets(), user.getVariablePresets());
        data.calculateAverageMonthlyIncome(recentEntries);

        return new HTTPResponse(200, "Ok", data);
    }
}
