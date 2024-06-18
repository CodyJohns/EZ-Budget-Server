package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.exceptions.AccessDeniedException;
import app.ezbudget.server.ezbudgetserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTests extends BaseTest {

    private UserService service;

    @BeforeEach
    void before() {
        this.setup();
    }

    @Override
    public void otherSetup() {
        service = new UserService(factory);
    }

    @Test
    void testLogin() {
        assertDoesNotThrow(() -> {
            service.loginUser(user.getUsername(), "1234567890");
        });
    }

    @Test
    void testLoginWithEmail() {
        assertDoesNotThrow(() -> {
            service.loginUser(user.getEmail(), "1234567890");
        });
    }

    @Test
    void testUserDoesNotExist() {
        assertThrows(NullPointerException.class, () -> {
            service.loginUser("meep", "1234");
        });
    }

    @Test
    void wrongPassword() {
        assertThrows(AccessDeniedException.class, () -> {
            service.loginUser(user.getUsername(), "1234");
        });
    }

    @Test
    void userNotVerified() {
        user.setVerified(false);
        user.setVerifyCode("1234");

        assertThrows(AccessDeniedException.class, () -> {
            service.loginUser(user.getUsername(), "1234");
        });
    }
}
