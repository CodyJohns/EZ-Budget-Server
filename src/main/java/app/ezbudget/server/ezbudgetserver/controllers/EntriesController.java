package app.ezbudget.server.ezbudgetserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.Entry;
import app.ezbudget.server.ezbudgetserver.service.ChartDataService;
import app.ezbudget.server.ezbudgetserver.service.EntryService;

@RestController
@RequestMapping("/api/v2/entries")
public class EntriesController {

    private final DAOFactory factory;
    private final Gson gson;

    public EntriesController(DAOFactory factory) {
        this.factory = factory;
        this.gson = new Gson();
    }

    @GetMapping("/all")
    public ResponseEntity<String> allEntries(@RequestHeader("Authorization") String authtoken) {

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getEntries(authtoken).getData()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all/paged/{page}")
    public ResponseEntity<String> allEntriesPaged(@RequestHeader("Authorization") String authtoken, @PathVariable("page") int page) {

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getEntriesPaged(authtoken, page).getData()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<String> fullEntry(@RequestHeader("Authorization") String authtoken, @PathVariable("id") String id) {

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getEntry(authtoken, id).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEntry(@RequestHeader("Authorization") String authtoken, @RequestParam("json") String json) {

        Entry entry = gson.fromJson(json, Entry.class);

        if(entry == null)
            return ResponseEntity.badRequest().build();

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.addEntry(authtoken, entry).getMessage()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateEntry(@RequestHeader("Authorization") String authtoken, @RequestParam("json") String json) {

        Entry entry = gson.fromJson(json, Entry.class);

        if(entry == null)
            return ResponseEntity.badRequest().build();

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.updateEntry(authtoken, entry).getMessage()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEntry(@RequestHeader("Authorization") String authtoken, @PathVariable("id") String id) {

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.deleteEntry(authtoken, id).getMessage()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/marked/{id}")
    public ResponseEntity<String> isEntryMarked(@RequestHeader("Authorization") String authtoken, @PathVariable("id") String id) {

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getMarked(authtoken, id).getData()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/mark")
    public ResponseEntity<String> markEntry(@RequestHeader("Authorization") String authtoken, @RequestParam("id") String id, @RequestParam("marked") boolean marked) {

        EntryService service = new EntryService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.markEntry(authtoken, id, marked).getMessage()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/item/history/{name}")
    public ResponseEntity<String> itemHistory(@RequestHeader("Authorization") String authtoken, @PathVariable("name") String name) {

        ChartDataService service = new ChartDataService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getItemHistory(authtoken, name).getData()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}