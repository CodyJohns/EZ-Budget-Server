package app.ezbudget.server.ezbudgetserver.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.CalculatedExpense;
import app.ezbudget.server.ezbudgetserver.model.NameEdit;
import app.ezbudget.server.ezbudgetserver.model.VariableExpense;
import app.ezbudget.server.ezbudgetserver.service.BudgetService;

@RestController
@RequestMapping("/api/v2/presets")
public class PresetsController {

    private final DAOFactory factory;
    private final Gson gson;

    public PresetsController(DAOFactory factory) {
        this.factory = factory;
        this.gson = new Gson();
    }

    @GetMapping("/overview")
    public ResponseEntity<String> budgetOverview(@RequestHeader("Authorization") String authtoken) {

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getBudgetOverview(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/calculated")
    public ResponseEntity<String> calculatedPresets(@RequestHeader("Authorization") String authtoken) {

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getCalculatedExpenses(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/variable")
    public ResponseEntity<String> variablePresets(@RequestHeader("Authorization") String authtoken) {

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getVariableExpenses(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/update/calculated")
    public ResponseEntity<String> updateCalculatedPresets(@RequestHeader("Authorization") String authtoken, @RequestParam("json") String json) {

        List<CalculatedExpense> presets = gson.fromJson(json, new TypeToken<List<CalculatedExpense>>() {}.getType());

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.updateCalculatedExpenses(authtoken, presets).getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/update/variable")
    public ResponseEntity<String> updateVariablePresets(@RequestHeader("Authorization") String authtoken, @RequestParam("json") String json) {

        List<VariableExpense> presets = gson.fromJson(json, new TypeToken<List<VariableExpense>>() {}.getType());

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.updateVariableExpenses(authtoken, presets).getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/update/variable/v2")
    public ResponseEntity<String> updateVariablePresetsV2(@RequestHeader("Authorization") String authtoken, @RequestParam("json") String json, @RequestParam("name_edits") String name_edits) {

        List<VariableExpense> presets = gson.fromJson(json, new TypeToken<List<VariableExpense>>() {}.getType());
        List<NameEdit> nameEdits = gson.fromJson(name_edits, new TypeToken<List<NameEdit>>() {}.getType());

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.updateVariableExpensesV2(authtoken, presets, nameEdits).getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/variable/amounts")
    public ResponseEntity<String> getVariableExpensesWithAmounts(@RequestHeader("Authorization") String authtoken) {

        BudgetService service = new BudgetService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getVariableExpensesWithTotalPurchasesAmount(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}