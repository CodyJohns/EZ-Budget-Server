package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.thirdPartyLogin.Verifier;
import app.ezbudget.server.ezbudgetserver.util.Mailer;

public interface DAOFactory {
    /**
     * Get the UserDAO object.
     * 
     * @return UserDAO
     */
    UserDAO getUserDAO();

    /**
     * Get the EntryDAO object.
     * 
     * @return EntryDAO
     */
    EntryDAO getEntryDAO();

    /**
     * Get the PurchaseDAO object.
     * 
     * @return PurchaseDAO
     */
    PurchaseDAO getPurchaseDAO();

    /**
     * Get the mailer object.
     * 
     * @return Mailer
     */
    Mailer getMailer();

    /**
     * Get the database object that contains the database handler or connection.
     * 
     * @return Database
     */
    Database getDatabase();

    /**
     * Get the current supported 3rd party login verfier.
     * 
     * @return Verifer
     */
    Verifier getThirdPartyLoginVerifier();

    /**
     * Get the transactionDAO object.
     * 
     * @return TransactionDAO
     */
    TransactionDAO getTransactionDAO();
}
