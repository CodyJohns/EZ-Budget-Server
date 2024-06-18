package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.exceptions.OperationViolationException;
import app.ezbudget.server.ezbudgetserver.exceptions.SendMailException;
import app.ezbudget.server.ezbudgetserver.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterTests extends BaseTest {

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
    void testRegister() {
        Mockito.when(factory.getUserDAO().userExists(user.getUsername())).thenReturn(false);

        assertDoesNotThrow(() -> {
            service.registerUser(user.getEmail(), user.getUsername(), "1234567890");
        });
    }

    @Test
    void testRegisterUserExists() {
        Mockito.when(factory.getUserDAO().userExists(user.getUsername())).thenReturn(true);

        assertThrows(OperationViolationException.class, () -> {
           service.registerUser(user.getEmail(), user.getUsername(), "1234567890");
        });
    }

    @Test
    void testRegisterEmailFail() {
        Mockito.when(factory.getUserDAO().userExists(user.getUsername())).thenReturn(false);
        Mockito.when(factory.getMailer().sendMail(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);

        assertThrows(SendMailException.class, () -> {
            service.registerUser(user.getEmail(), user.getUsername(), "1234567890");
        });
    }
}
