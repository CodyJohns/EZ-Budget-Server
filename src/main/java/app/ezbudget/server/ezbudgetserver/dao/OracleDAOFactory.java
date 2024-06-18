package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.database.OracleNoSQLDatabase;
import app.ezbudget.server.ezbudgetserver.thirdPartyLogin.GoogleVerifier;
import app.ezbudget.server.ezbudgetserver.thirdPartyLogin.Verifier;
import app.ezbudget.server.ezbudgetserver.util.JavaMailer;
import app.ezbudget.server.ezbudgetserver.util.Mailer;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@Component
public class OracleDAOFactory implements DAOFactory {

    private UserDAO userDAO;
    private EntryDAO entryDAO;
    private PurchaseDAO purchaseDAO;
    private Mailer mailer;
    private Database database;
    private Verifier<GoogleIdToken> tokenVerifier;

    public Database getDatabase() {
        if(database == null) {
            try {
                database = new OracleNoSQLDatabase();
                database.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return database;
    }

    @Override
    public UserDAO getUserDAO() {
        if(userDAO == null)
            userDAO = new OracleUserDAO(getDatabase());

        return userDAO;
    }

    @Override
    public EntryDAO getEntryDAO() {
        if(entryDAO == null)
            entryDAO = new OracleEntryDAO(getDatabase());

        return entryDAO;
    }

    @Override
    public PurchaseDAO getPurchaseDAO() {
        if(purchaseDAO == null)
            purchaseDAO = new OraclePurchaseDAO(getDatabase());

        return purchaseDAO;
    }

    @Override
    public Mailer getMailer() {
        if(mailer == null)
            mailer = new JavaMailer();

        return mailer;
    }

    @Override
    public Verifier<GoogleIdToken> getThirdPartyLoginVerifier() {
        if(tokenVerifier == null)
            tokenVerifier = new GoogleVerifier();

        return tokenVerifier;
    }
}
