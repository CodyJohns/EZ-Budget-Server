package app.ezbudget.server.ezbudgetserver.exceptions;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String msg) {
        super(msg);
    }
}
