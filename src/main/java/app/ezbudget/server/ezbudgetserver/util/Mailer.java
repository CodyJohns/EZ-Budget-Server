package app.ezbudget.server.ezbudgetserver.util;

public interface Mailer {
    boolean sendLoginCode(String recip, String code);

    boolean sendMail(String subject, String body, String altbody, String recip);
}
