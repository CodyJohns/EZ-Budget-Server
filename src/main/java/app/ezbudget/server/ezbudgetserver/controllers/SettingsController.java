package app.ezbudget.server.ezbudgetserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.service.AccountService;

@RestController
@RequestMapping("/api/v2/settings")
public class SettingsController {

    private final DAOFactory factory;
    private final Gson gson;

    public SettingsController(DAOFactory factory) {
        this.factory = factory;
        this.gson = new Gson();
    }

    @GetMapping("/emails")
    public ResponseEntity<String> emailsEnabled(@RequestHeader("Authorization") String authtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getEmailsEnabled(authtoken).getData()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/emails/enable")
    public ResponseEntity<String> setEmailsEnabled(@RequestHeader("Authorization") String authtoken, @RequestParam("value") boolean value) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.setEmailsEnabled(authtoken, value).getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}