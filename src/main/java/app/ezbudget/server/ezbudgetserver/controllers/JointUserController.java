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
@RequestMapping("/api/v2/user/joint")
public class JointUserController {

    private final DAOFactory factory;
    private final Gson gson;

    public JointUserController(DAOFactory factory) {
        this.factory = factory;
        this.gson = new Gson();
    }

    @GetMapping("/status")
    public ResponseEntity<String> getJointStatus(@RequestHeader("Authorization") String authtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getJointAccountStatus(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<String> allJointUsers(@RequestHeader("Authorization") String authtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getJointAccountHolders(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/pending/all")
    public ResponseEntity<String> allPendingJointRequests(@RequestHeader("Authorization") String authtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getPendingJointAccountRequests(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/sent/all")
    public ResponseEntity<String> allSentJointRequests(@RequestHeader("Authorization") String authtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.getSentJointAccountRequests(authtoken).getData()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelSentJointRequests(@RequestHeader("Authorization") String authtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.cancelSentJointAccountRequests(authtoken).getMessage()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptJointRequests(@RequestHeader("Authorization") String hostAuthtoken, @RequestParam("requestor") String requestorAuthtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.acceptJointAccountHolderRequest(hostAuthtoken, requestorAuthtoken).getMessage()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectJointRequests(@RequestHeader("Authorization") String hostAuthtoken, @RequestParam("requestor") String requestorAuthtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.rejectJointAccountHolderRequest(hostAuthtoken, requestorAuthtoken).getMessage()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeJointAccount_JointPerspective(@RequestHeader("Authorization") String requestorAuthtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.removeJointAccountHolder_JointPerspective(requestorAuthtoken).getMessage()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/host/remove")
    public ResponseEntity<String> removeJointAccount_HostPerspective(@RequestHeader("Authorization") String hostAuthtoken, @RequestParam("requestor") String requestorAuthtoken) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.removeJointAccountHolder_HostPerspective(hostAuthtoken, requestorAuthtoken).getMessage()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendJointAccountRequest(@RequestHeader("Authorization") String requestorAuthtoken, @RequestParam("holder") String holderUsername) {

        AccountService service = new AccountService(factory);

        try {
            return ResponseEntity.ok(gson.toJson(service.sendJointAccountHolderRequest(requestorAuthtoken, holderUsername).getMessage()));
        } catch(NullPointerException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}