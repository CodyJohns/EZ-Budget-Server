package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.*;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

import java.util.ArrayList;
import java.util.List;

public class BudgetService extends JointService {

    private final int NUM_ENTRIES = 12;

    public BudgetService(DAOFactory factory) {
        super(factory);
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

    public HTTPResponse updateVariableExpenses(String authtoken, List<VariableExpense> presets) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        user.setVariablePresets(presets);

        this.factory.getUserDAO().save(user);

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
