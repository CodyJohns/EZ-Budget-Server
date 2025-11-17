package app.ezbudget.server.ezbudgetserver.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppleSiteAssociationController {

    @GetMapping("/apple-app-site-association")
    public ResponseEntity<Map<String, ?>> appAssociation() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "applinks", Map.of(
                                "paths", List.of(Map.of(
                                        "appID", "S35T4S9R5L.org.name.ezincometracker",
                                        "paths", List.of("/api/v2/plaid/oauth/*"))))));
    }

    @GetMapping("/.well-known/apple-app-site-association")
    public ResponseEntity<Map<String, ?>> appAssociation2() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "applinks", Map.of(
                                "paths", List.of(Map.of(
                                        "appID", "S35T4S9R5L.org.name.ezincometracker",
                                        "paths", List.of("/api/v2/plaid/oauth/*"))))));
    }
}
