package app.ezbudget.server.ezbudgetserver.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.exceptions.AccessDeniedException;
import app.ezbudget.server.ezbudgetserver.exceptions.DailyItemNotFoundException;
import app.ezbudget.server.ezbudgetserver.exceptions.OperationViolationException;
import app.ezbudget.server.ezbudgetserver.model.Purchase;
import app.ezbudget.server.ezbudgetserver.service.AccountService;
import app.ezbudget.server.ezbudgetserver.service.ChartDataService;
import app.ezbudget.server.ezbudgetserver.service.PurchasesService;
import app.ezbudget.server.ezbudgetserver.service.UserService;

@RestController
@RequestMapping("/api/v2/user")
public class UserController {

    private final DAOFactory factory;
    private final Gson gson;

    public UserController(DAOFactory factory) {
        this.factory = factory;
        this.gson = new Gson();
    }

    @GetMapping("/income-total")
    public ResponseEntity<String> getUserIncomeHistory(@RequestHeader("Authorization") String authtoken) {

        ChartDataService service = new ChartDataService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getIncomeYearly(authtoken).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/month-expense-trend")
    public ResponseEntity<String> getMonthlyExpenseTrend(@RequestHeader("Authorization") String authtoken) {
        ChartDataService service = new ChartDataService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getMonthlyExpenseTrend(authtoken).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/verify/{code}")
    public ResponseEntity<String> verifyUser(@PathVariable("code") String code) {

        UserService service = new UserService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.verifyUser(code).getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam("username") String username,
            @RequestParam("password") String password) {

        UserService service = new UserService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.loginUser(username, password)));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/google/login")
    public ResponseEntity<String> loginUserViaGoogle(@RequestParam("google_token") String googleToken) {
        UserService service = new UserService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.loginUserViaGoogle(googleToken).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam("email") String email,
            @RequestParam("username") String username, @RequestParam("password") String password) {

        UserService service = new UserService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.registerUser(email, username, password).getMessage()));
        } catch (OperationViolationException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/password/update")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String authtoken,
            @RequestParam("oldpassword") String oldpassword, @RequestParam("newpassword") String newpassword) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity
                    .ok(gson.toJson(service.updatePassword(authtoken, oldpassword, newpassword).getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/password/init")
    public ResponseEntity<String> initPasswordChange(@RequestParam("identifier") String identifier) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.initiatePasswordChange(identifier).getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/password/auth")
    public ResponseEntity<String> authPasswordChange(@RequestParam("code") String code) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.authPasswordChange(code).getMessage()));
        } catch (NullPointerException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/password/comp")
    public ResponseEntity<String> compPasswordChange(@RequestParam("code") String code,
            @RequestParam("password") String password) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.completePasswordChange(code, password).getMessage()));
        } catch (NullPointerException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/purchases/all")
    public ResponseEntity<String> getPurchasedItems(@RequestHeader("Authorization") String authtoken) {

        PurchasesService service = new PurchasesService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getPurchases(authtoken).getData()));
        } catch (NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/purchases/save")
    public ResponseEntity<String> savePurchasedItems(@RequestHeader("Authorization") String authtoken,
            @RequestParam("key") String key, @RequestParam("json") String json) {

        PurchasesService service = new PurchasesService(factory);

        List<Purchase> itemsList = gson.fromJson(json, new TypeToken<List<Purchase>>() {
        }.getType());

        try {
            return ResponseEntity.ok(gson.toJson(service.savePurchases(authtoken, key, itemsList).getMessage()));
        } catch (NullPointerException | DailyItemNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}