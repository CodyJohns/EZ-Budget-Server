package app.ezbudget.server.ezbudgetserver.exceptions;

public class AccessDeniedException extends Exception {
    public AccessDeniedException(String msg) {
        super(msg);
    }
}
