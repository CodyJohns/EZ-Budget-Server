package app.ezbudget.server.ezbudgetserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.service.PlaidService;

@RestController
@RequestMapping("/api/v2/plaid")
public class PlaidController {
    private final DAOFactory factory;
    private final Gson gson;

    public PlaidController(DAOFactory factory) {
        this.factory = factory;
        this.gson = new Gson();
    }

    @GetMapping("/link-create")
    public ResponseEntity<String> getUserIncomeHistory(@RequestHeader("Authorization") String authtoken) {

        PlaidService service = new PlaidService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getLinkToken(authtoken).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
