package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.model.*;
import app.ezbudget.server.ezbudgetserver.service.ChartDataService;
import app.ezbudget.server.ezbudgetserver.service.EntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EntryTests extends BaseTest {

    private EntryService service;
    private ChartDataService chartService;
    private List<Entry> entries;
    private Entry entry;

    @BeforeEach
    void before() {
        this.setup();
    }

    @Override
    public void otherSetup() {
        service = new EntryService(factory);
        chartService = new ChartDataService(factory);

        entry = new Entry("1234", "Jan", 1970, 3000, 150, 2850, 456.35F);
        entries = new ArrayList<>();

        Entry one = new Entry("1234", "Jan", 1970, 3000, 150, 2850, 456.35F);
        one.addCalculatedExpense(new CalculatedExpense(0, "Test", 100, CalculatedExpense.EXACT, ""));

        Entry two = new Entry("1235", "Feb", 1970, 3000, 150, 2850, 456.35F);
        two.addVariableExpense(new VariableExpense(0, "Test", 35, 30));

        Entry three = new Entry("1236", "Mar", 1970, 3000, 150, 2850, 456.35F);
        three.addCalculatedExpense(new CalculatedExpense(0, "Test", 70, CalculatedExpense.EXACT, ""));

        entries.add(one);
        entries.add(two);
        entries.add(three);

        Mockito.when(entryDAO.getAllEntries(Mockito.any())).thenReturn(entries);
        Mockito.when(entryDAO.getEntry(Mockito.any(), Mockito.any())).thenReturn(entry);
        Mockito.when(entryDAO.getSpecificNumberOfEntries(user.getAuthtoken(), 12)).thenReturn(entries);
        Mockito.when(entryDAO.getSpecificNumberOfEntries(user.getAuthtoken(), 0)).thenReturn(new ArrayList<>());
    }

    @Test
    void testEntries() {
        BasicEntryWrapper wrapper = (BasicEntryWrapper) service.getEntries(user.getAuthtoken()).getData();
        assertEquals(3, wrapper.entries.size());
        assertFalse(wrapper.hasMore);
    }

    @Test
    void testEntriesPaged() {
        BasicEntryWrapper wrapper = (BasicEntryWrapper) service.getEntriesPaged(user.getAuthtoken(), 1).getData();
        assertEquals(3, wrapper.entries.size());
        assertFalse(wrapper.hasMore);
    }

    @Test
    void testEntriesPagedZero() {
        BasicEntryWrapper wrapper = (BasicEntryWrapper) service.getEntriesPaged(user.getAuthtoken(), 0).getData();
        assertEquals(0, wrapper.entries.size());
        assertFalse(wrapper.hasMore);
    }

    @Test
    void testEntry() {
        List<Entry> list = (List<Entry>) service.getEntry(user.getAuthtoken(), "1234").getData();
        assertEquals(1, list.size());
        assertEquals(entry, list.get(0));
    }

    @Test
    void testMarked() {
        entry.setMarked(true);

        Map<String, Boolean> data = (Map<String, Boolean>) service.getMarked(user.getAuthtoken(), "1234").getData();

        assertEquals(1, data.size());
        assertEquals(true, data.get("marked"));
    }

    @Test
    void testUnmarked() {
        entry.setMarked(false);

        Map<String, Boolean> data = (Map<String, Boolean>) service.getMarked(user.getAuthtoken(), "1234").getData();

        assertEquals(1, data.size());
        assertEquals(false, data.get("marked"));
    }

    @Test
    void testYearlyIncomeChart() {

        List<ChartData> data = (List<ChartData>) chartService.getIncomeYearly(user.getAuthtoken()).getData();
        assertEquals(3, data.size());
    }

    @Test
    void testItemHistory() {

        List<ExpenseHistoryItem> data = (List<ExpenseHistoryItem>) chartService.getItemHistory(user.getAuthtoken(), "Test").getData();
        assertEquals(3, data.size());
    }

    @Test
    void testNoItemHistory() {

        List<ExpenseHistoryItem> data = (List<ExpenseHistoryItem>) chartService.getItemHistory(user.getAuthtoken(), "Nope").getData();
        assertEquals(0, data.size());
    }
}
