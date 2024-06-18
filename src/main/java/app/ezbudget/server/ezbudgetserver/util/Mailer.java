package app.ezbudget.server.ezbudgetserver.util;

public interface Mailer {
    boolean sendMail(String subject, String body, String altbody, String recip);
}
