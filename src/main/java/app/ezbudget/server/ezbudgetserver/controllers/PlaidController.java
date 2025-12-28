package app.ezbudget.server.ezbudgetserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.plaid.PlaidTransactionUpdate;
import app.ezbudget.server.ezbudgetserver.service.PlaidService;
import app.ezbudget.server.ezbudgetserver.util.PlaidJWTVerifier;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v2/plaid")
public class PlaidController {
    private final DAOFactory factory;
    private final PlaidJWTVerifier verifier;
    private final Gson gson;

    public PlaidController(DAOFactory factory, PlaidJWTVerifier verifier) {
        this.factory = factory;
        this.verifier = verifier;
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

    @PostMapping("/save-public-token")
    public ResponseEntity<String> publicToken(@RequestHeader("Authorization") String authtoken,
            @RequestParam("public_token") String public_token) {

        if (public_token == null)
            return ResponseEntity.badRequest().build();

        PlaidService service = new PlaidService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.savePublicToken(authtoken, public_token).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> processPlaidWebhook(@RequestHeader("Plaid-Verification") String jwt,
            @RequestBody String body) {
        try {
            if (!verifier.verify(jwt, body))
                return ResponseEntity.badRequest().build();

            PlaidTransactionUpdate update = gson.fromJson(body, PlaidTransactionUpdate.class);
            PlaidService service = new PlaidService(factory);

            try {
                return ResponseEntity.ok(gson.toJson(service.processUpdate(update).getMessage()));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/items")
    public ResponseEntity<String> getUserPlaidItems(@RequestHeader("Authorization") String authtoken) {
        PlaidService service = new PlaidService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getUserPlaidItems(authtoken).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/oauth")
    public ResponseEntity getMethodName(@RequestParam String param) {
        return ResponseEntity.ok().build();
    }
}
