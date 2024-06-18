package app.ezbudget.server.ezbudgetserver.Tests;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.dao.EntryDAO;
import app.ezbudget.server.ezbudgetserver.dao.UserDAO;
import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.util.Mailer;
import org.mockito.Mockito;

public abstract class BaseTest {

    protected DAOFactory factory;
    protected UserDAO userDAO;
    protected EntryDAO entryDAO;
    protected Database database;
    protected Mailer mailer;
    protected User user = new User("testuser", "1234567890", "0987654321", "test@test");

    protected void setup() {
        database = Mockito.mock();
        factory = Mockito.mock();
        Mockito.when(factory.getDatabase()).thenReturn(database);

        userDAO = Mockito.mock();
        entryDAO = Mockito.mock();
        mailer = Mockito.mock();

        Mockito.when(factory.getUserDAO()).thenReturn(userDAO);
        Mockito.when(factory.getEntryDAO()).thenReturn(entryDAO);
        Mockito.when(factory.getMailer()).thenReturn(mailer);

        user.setVerifyCode("1");
        user.setVerified(true);
        user.setPasswordChangeCode("1");
        user.setAwaitingPasswordChange(false);

        Mockito.when(mailer.sendMail(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(userDAO.getUserByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(userDAO.getUserByPasswordChangeCode(Mockito.anyString())).thenReturn(user);
        Mockito.when(userDAO.getUserByAuthtoken(Mockito.anyString())).thenReturn(user);
        Mockito.when(userDAO.getUserByUsername("meep")).thenThrow(NullPointerException.class);
        Mockito.when(userDAO.getUserByEmail("meep")).thenThrow(NullPointerException.class);

        this.otherSetup();
    }

    public abstract void otherSetup();
}
