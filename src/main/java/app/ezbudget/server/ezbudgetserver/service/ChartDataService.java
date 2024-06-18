package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.*;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

import java.util.ArrayList;
import java.util.List;

public class ChartDataService extends JointService {

    private final int NUM_ENTRIES = 12;

    public ChartDataService(DAOFactory factory) {
        super(factory);
    }

    public HTTPResponse getIncomeYearly(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<Entry> entries = this.factory.getEntryDAO().getSpecificNumberOfEntries(user.getAuthtoken(), NUM_ENTRIES);

        List<ChartData> data = new ArrayList<>();

        for (Entry entry : entries) {
            data.add(new ChartData(entry.getMonth(), entry.getGross()));
        }

        return new HTTPResponse(200, "Ok", data);
    }

    /**
     * Get item history using a specified search term, or item name. 
     * Uses the latest n number of entries.
     * 
     * @param authtoken
     * @param item
     * @return HTTPResponse object
     */
    public HTTPResponse getItemHistory(String authtoken, String item) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<Entry> entries = this.factory.getEntryDAO().getSpecificNumberOfEntries(user.getAuthtoken(), NUM_ENTRIES);

        List<ExpenseHistoryItem> data = new ArrayList<>();

        int count_id = 0;
        for (Entry entry : entries) {

            boolean found = false;

            for (CalculatedExpense expense : entry.getCalculatedExpenses()) {

                if (!expense.getName().equals(item))
                    continue;

                data.add(new ExpenseHistoryItem(count_id, expense.getName(),
                        expense.getAmount(), entry.getMonth(), entry.getYear()));

                found = true;
                count_id++;
            }

            if(found) continue;

            for (VariableExpense expense : entry.getVariableExpenses()) {

                if(!expense.getName().equals(item))
                    continue;

                data.add(new ExpenseHistoryItem(count_id, expense.getName(),
                        expense.getAmount(), entry.getMonth(), entry.getYear()));

                count_id++;
            }
        }

        return new HTTPResponse(200, "Ok", data);
    }
}
