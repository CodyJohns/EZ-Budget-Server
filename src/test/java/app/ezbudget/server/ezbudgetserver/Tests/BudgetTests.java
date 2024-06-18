package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.model.BudgetOverview;
import app.ezbudget.server.ezbudgetserver.model.CalculatedExpense;
import app.ezbudget.server.ezbudgetserver.model.Entry;
import app.ezbudget.server.ezbudgetserver.model.VariableExpense;
import app.ezbudget.server.ezbudgetserver.service.BudgetService;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetTests extends BaseTest {

    private List<Entry> entries;
    private BudgetService service;

    @BeforeEach
    void before() {
        this.setup();
    }

    @Override
    public void otherSetup() {
        service = new BudgetService(factory);

        entries = new ArrayList<>();

        Entry one = new Entry("1234", "Jan", 1970, 5678.89F, 150, 2850, 456.35F);
        one.addCalculatedExpense(new CalculatedExpense(0, "Test", 100, CalculatedExpense.EXACT, ""));

        Entry two = new Entry("1235", "Feb", 1970, 3563.90F, 150, 2850, 456.35F);
        two.addVariableExpense(new VariableExpense(0, "Test", 35, 30));

        Entry three = new Entry("1236", "Mar", 1970, 3190.01F, 150, 2850, 456.35F);
        three.addCalculatedExpense(new CalculatedExpense(0, "Test", 70, CalculatedExpense.EXACT, ""));

        entries.add(one);
        entries.add(two);
        entries.add(three);

        CalculatedExpense calculatedExpense = new CalculatedExpense(0, "Test 1", 100, "exact", "");
        VariableExpense variableExpense = new VariableExpense(0, "Test 2", 200, 200);

        user.getCalculatedPresets().add(calculatedExpense);
        user.getVariablePresets().add(variableExpense);

        Mockito.when(entryDAO.getSpecificNumberOfEntries(user.getAuthtoken(), 12)).thenReturn(entries);
    }

    @Test
    void testOverview() {
        HTTPResponse response = service.getBudgetOverview(user.getAuthtoken());

        BudgetOverview data = (BudgetOverview) response.getData();

        float avg = 5678.89F;
        avg += 3563.90F;
        avg += 3190.01F;
        float actualavg = (avg / 3);

        assertEquals(actualavg, data.averageMonthlyIncome);
        assertEquals(1, data.calculatedExpenses.size());
        assertEquals(1, data.variableExpenses.size());
    }

    @Test
    void testOverviewUserNotFound() {
        Mockito.when(userDAO.getUserByAuthtoken(Mockito.anyString())).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> {
            HTTPResponse response = service.getBudgetOverview(user.getAuthtoken());
        });
    }

    @Test
    void testOverviewNoEntries() {
        Mockito.when(entryDAO.getSpecificNumberOfEntries(user.getAuthtoken(), 12)).thenReturn(new ArrayList<>());

        Gson gson = new Gson();

        assertDoesNotThrow(() -> {
            HTTPResponse response = service.getBudgetOverview(user.getAuthtoken());
            System.out.println(gson.toJson(response.getData()));
        });
    }
}
