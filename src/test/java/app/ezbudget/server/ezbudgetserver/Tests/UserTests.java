package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.exceptions.AccessDeniedException;
import app.ezbudget.server.ezbudgetserver.exceptions.SendMailException;
import app.ezbudget.server.ezbudgetserver.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests extends BaseTest {

    private AccountService service;

    @BeforeEach
    void before() {
        this.setup();
    }

    @Override
    public void otherSetup() {
        service = new AccountService(factory);
    }

    @Test
    void testInitPasswordChange() {

        try {
            service.initiatePasswordChange(user.getUsername());

            assertTrue(user.awaitingPasswordChange());
            assertEquals(8, user.getPasswordChangeCode().length());
        } catch (SendMailException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCompletePasswordChange() {

        service.completePasswordChange("1234", "1234");

        assertFalse(user.awaitingPasswordChange());
        assertEquals("1", user.getPasswordChangeCode());
    }

    @Test
    void testUpdatePassword() {

        assertDoesNotThrow(() -> {
            service.updatePassword(user.getAuthtoken(), "1234567890", "0987654321");
        });

        assertTrue(user.getPassword().compare("0987654321"));
    }

    @Test
    void testUpdatePasswordWrongPassword() {

        assertThrows(AccessDeniedException.class, () -> {
            service.updatePassword(user.getAuthtoken(), "0987654321", "0987654321");
        });
    }

    @Test
    void testUpdatePasswordFailedEmail() {
        Mockito.when(mailer.sendMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        assertThrows(SendMailException.class, () -> {
            service.updatePassword(user.getAuthtoken(), "1234567890", "0987654321");
        });
    }
}
