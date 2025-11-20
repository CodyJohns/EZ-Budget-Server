package app.ezbudget.server.ezbudgetserver.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class JavaMailer implements Mailer {

    private Session session;

    public JavaMailer() {
        session = Session.getInstance(setupConnection(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        System.getenv("MAIL_USER"),
                        System.getenv("MAIL_PASS"));
            }
        });
    }

    private Properties setupConnection() {
        Properties props = new Properties();

        props.put("mail.smtp.host", System.getenv("MAIL_HOST"));
        props.put("mail.smtp.port", System.getenv("MAIL_PORT"));
        props.put("mail.smtp.auth", "true"); // if authentication is required
        props.put("mail.smtp.starttls.enable", "true");

        return props;
    }

    @Override
    public boolean sendLoginCode(String recip, String code) {

        String htmlbody = "<div style=\"padding: 50px\"><div style=\"max-width: 700px; font-family: Arial, sans-serif\"><div style=\"min-height: 74px;border: 1px solid #111111;padding: 3px;display: flex;align-items: center;justify-content: center;\"><img src=\"https://ezbudget.app/public_assets/ez.png\" height=\"74px\" /></div><div style=\"padding: 40px;border-left: 1px solid #111111;border-right: 1px solid #111111;\"><h2 style=\"text-align: center\">Your login verification code.</h2><div style=\"font-size: 20px; text-align: center;\">One-time code:<strong>"
                + code
                + "</strong></div><p>This code will expire in 10 minutes. If you didn’t request this, you can safely ignore this email.</p></div><div style=\"padding: 40px;border: 1px solid #111111;background-color: #eaeaea;\"><p>This email was sent due to account action on your part. We will not send you these types of emails unless you have requested it byinteracting with our website.</p><p>&copy; 2025 EZ Budget by <a href=\"https://western-solutions.dev\">Western Software Solutions</a></p></div></div></div>";

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@ezbudget.app"));
            message.setSubject("Your EZ Budget login code");
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recip));

            Multipart multipart = new MimeMultipart();

            MimeBodyPart html = new MimeBodyPart();
            html.setContent(htmlbody, "text/html");
            multipart.addBodyPart(html);

            MimeBodyPart plainText = new MimeBodyPart();
            plainText.setText("Your EZ Budget login code is: " + code
                    + ". This code will expire in 10 minutes. If you didn't request this, you can safely ignore this email.");
            multipart.addBodyPart(plainText);

            message.setContent(multipart);

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    @Override
    public boolean sendMail(String subject, String body, String altbody, String recip) {

        String htmlbody = "<div style=\"padding: 50px\"><div style=\"max-width: 700px; font-family: Arial, sans-serif\"><div style=\"min-height: 74px;border: 1px solid #111111;padding: 3px;display: flex;align-items: center;justify-content: center;\"><img src=\"https://ezbudget.app/public_assets/ez.png\" height=\"74px\" /></div><div style=\"padding: 40px;border-left: 1px solid #111111;border-right: 1px solid #111111;\"><h3 style=\"text-align: center\">"
                + subject + "</h3><p>" + body
                + "</p></div><div style=\"padding: 40px;border: 1px solid #111111;background-color: #eaeaea;\"><p>This email was sent due to account action on your part. We will not send you these types of emails unless you have requested it by interacting with our website.</p><p>&copy; 2025 EZ Budget by <a href=\"https://western-solutions.dev\">Western Software Solutions</a></p></div></div></div>";

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@ezbudget.app"));
            message.setSubject(subject);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recip));

            Multipart multipart = new MimeMultipart();

            MimeBodyPart html = new MimeBodyPart();
            html.setContent(htmlbody, "text/html");
            multipart.addBodyPart(html);

            MimeBodyPart plainText = new MimeBodyPart();
            plainText.setText(altbody);
            multipart.addBodyPart(plainText);

            message.setContent(multipart);

            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
