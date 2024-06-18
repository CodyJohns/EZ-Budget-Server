package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.dao.PurchaseDAO;
import app.ezbudget.server.ezbudgetserver.exceptions.DailyItemNotFoundException;
import app.ezbudget.server.ezbudgetserver.model.Purchase;
import app.ezbudget.server.ezbudgetserver.model.PurchasedExpense;
import app.ezbudget.server.ezbudgetserver.model.VariableExpense;
import app.ezbudget.server.ezbudgetserver.service.PurchasesService;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchasesTests extends BaseTest {

    private PurchaseDAO pDAO;

    @Override
    public void otherSetup() {
        pDAO = mock(PurchaseDAO.class);

        List<Purchase> purchases = List.of(
                new Purchase(0, "one", 10F, "1/30"),
                new Purchase(1, "two", 20F, "1/31"),
                new Purchase(2, "three", 30F, "1/1")
        );

        Map<String, PurchasedExpense> sample = Map.of(
                "Test1", new PurchasedExpense(0, "Test1", 60F, 70F, purchases),
                "Test2", new PurchasedExpense(1, "Test2", 60F, 60F, purchases),
                "Test3", new PurchasedExpense(2, "Test3", 60F, 80F, purchases)
        );

        when(pDAO.getExpensesWithPurchases(Mockito.anyString())).thenReturn(sample);

        when(factory.getPurchaseDAO()).thenReturn(pDAO);
    }

    @BeforeEach
    void before() {
        this.setup();
    }

    @Test
    void testDAO() {
        Map<String, PurchasedExpense> output = pDAO.getExpensesWithPurchases(Mockito.anyString());

        assertNotNull(output);
        assertEquals(3, output.size());
        assertEquals(3, output.values().size());
    }

    @Test
    void testGetPurchases() {

        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 10F),
                new VariableExpense(1, "Test2", 0F, 20F),
                new VariableExpense(2, "Test3", 0F, 30F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().containsKey("Test1"));
        assertTrue(response.getData().containsKey("Test2"));
        assertTrue(response.getData().containsKey("Test3"));
        assertFalse(response.getData().containsKey("Test4"));
    }

    @Test
    void testGetPurchasesNull() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 10F),
                new VariableExpense(1, "Test2", 0F, 20F),
                new VariableExpense(2, "Test3", 0F, 30F)
        );

        when(pDAO.getExpensesWithPurchases(Mockito.anyString())).thenThrow(NullPointerException.class);

        user.variable_presets = List.of(
                new VariableExpense(0, "Test1a", 0F, 10F),
                new VariableExpense(1, "Test2a", 0F, 20F),
                new VariableExpense(2, "Test3a", 0F, 30F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().containsKey("Test1a"));
        assertTrue(response.getData().get("Test1a").getPurchases().isEmpty());
        assertTrue(response.getData().containsKey("Test2a"));
        assertTrue(response.getData().get("Test2a").getPurchases().isEmpty());
        assertTrue(response.getData().containsKey("Test3a"));
        assertTrue(response.getData().get("Test3a").getPurchases().isEmpty());

    }

    @Test
    void testGetPurchasesExtraKey() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 10F),
                new VariableExpense(1, "Test2", 0F, 20F),
                new VariableExpense(2, "Test3", 0F, 30F),
                new VariableExpense(0, "Test4", 0F, 10F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().containsKey("Test1"));
        assertFalse(response.getData().get("Test1").getPurchases().isEmpty());
        assertTrue(response.getData().containsKey("Test2"));
        assertFalse(response.getData().get("Test2").getPurchases().isEmpty());
        assertTrue(response.getData().containsKey("Test3"));
        assertFalse(response.getData().get("Test3").getPurchases().isEmpty());
        assertTrue(response.getData().containsKey("Test4"));
        assertTrue(response.getData().get("Test4").getPurchases().isEmpty());
    }

    @Test
    void testGetPurchasesMissingKey() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 10F),
                new VariableExpense(1, "Test2", 0F, 20F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().containsKey("Test1"));
        assertFalse(response.getData().get("Test1").getPurchases().isEmpty());
        assertTrue(response.getData().containsKey("Test2"));
        assertFalse(response.getData().get("Test2").getPurchases().isEmpty());
        assertFalse(response.getData().containsKey("Test3"));
    }

    @Test
    void testSaveNewItems() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 10F),
                new VariableExpense(1, "Test2", 0F, 20F)
        );

        List<Purchase> new_purchases = List.of(
                new Purchase(0, "Test6a", 35.67F, "4/1"),
                new Purchase(1, "Test7a", 113.10F, "4/2")
        );

        PurchasesService service = new PurchasesService(factory);

        assertDoesNotThrow(() -> {
            service.savePurchases(user.getAuthtoken(), "Test1", new_purchases);
        });
    }

    @Test
    void testSaveNewItemsKeyNotFound() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 10F),
                new VariableExpense(1, "Test2", 0F, 20F)
        );

        List<Purchase> new_purchases = List.of(
                new Purchase(0, "Test6a", 35.67F, "5/12"),
                new Purchase(1, "Test7a", 113.10F, "5/13")
        );

        PurchasesService service = new PurchasesService(factory);

        assertThrows(DailyItemNotFoundException.class, () -> {
            service.savePurchases(user.getAuthtoken(), "Test123", new_purchases);
        });
    }

    @Test
    void testPurchasesVariableExpenseMaxChanged() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 50F),
                new VariableExpense(1, "Test2", 0F, 50F),
                new VariableExpense(2, "Test3", 0F, 50F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertEquals(50F, response.getData().get("Test1").getMax());
        assertEquals(50F, response.getData().get("Test2").getMax());
        assertEquals(50F, response.getData().get("Test3").getMax());
    }

    @Test
    void testPurchasesVariableExpenseMaxChangedAndMissingExpense() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 50F),
                new VariableExpense(1, "Test2", 0F, 50F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().containsKey("Test1"));
        assertEquals(50F, response.getData().get("Test1").getMax());
        assertTrue(response.getData().containsKey("Test2"));
        assertEquals(50F, response.getData().get("Test2").getMax());
        assertFalse(response.getData().containsKey("Test3"));
    }

    @Test
    void testPurchasesVariableExpenseMaxChangedAndExtraExpense() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 50F),
                new VariableExpense(1, "Test2", 0F, 50F),
                new VariableExpense(2, "Test3", 0F, 50F),
                new VariableExpense(3, "Test4", 0F, 50F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().containsKey("Test1"));
        assertEquals(50F, response.getData().get("Test1").getMax());
        assertTrue(response.getData().containsKey("Test2"));
        assertEquals(50F, response.getData().get("Test2").getMax());
        assertTrue(response.getData().containsKey("Test3"));
        assertEquals(50F, response.getData().get("Test3").getMax());
        assertTrue(response.getData().containsKey("Test4"));
        assertEquals(50F, response.getData().get("Test4").getMax());
    }

    @Test
    void testPurchasesVariableExpenseIdChanged() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 50F),
                new VariableExpense(1, "Test2", 0F, 50F),
                new VariableExpense(10, "Test3", 0F, 50F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertEquals(0, response.getData().get("Test1").getId());
        assertEquals(1, response.getData().get("Test2").getId());
        assertEquals(10, response.getData().get("Test3").getId());
    }

    @Test
    void testPurchasesVariableExpenseIsAccountChanged() {
        user.variable_presets = List.of(
                new VariableExpense(0, "Test1", 0F, 50F, true),
                new VariableExpense(1, "Test2", 0F, 50F),
                new VariableExpense(2, "Test3", 0F, 50F)
        );

        PurchasesService service = new PurchasesService(factory);

        HTTPResponse<Map<String, PurchasedExpense>> response = service.getPurchases(user.getAuthtoken());

        assertTrue(response.getData().get("Test1").isAccount());
        assertFalse(response.getData().get("Test2").isAccount());
        assertFalse(response.getData().get("Test3").isAccount());
    }
}
