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
                    System.getenv("MAIL_PASS")
                );
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
    public boolean sendMail(String subject, String body, String altbody, String recip) {

        String htmlbody = "<div style='max-width:800px;margin:0 auto;font-family: Arial, Helvetica, sans-serif;'><div style='width:100%;padding:20px;'><center><a href='https://ezbudget.app'><img src='https://ezbudget.app/public_assets/ez.png' height='60px' /></a></center></div><div style='max-width:500px;padding-bottom:60px;margin:0 auto;'><p>" + body + "</p></div><div style='padding:10px 40px;font-size:12px;color:#555;'><p>This email was sent due to account action on your part. We will not send you these types of emails unless you have requested it by interacting with our website.</p></div><div style='background: #f4f4f4;color:#555;padding:20px 40px;'><p>EZ Budget &copy; 2023. All rights reserved. All images and logos belong to ezbudget.app.<br />Customer Service: <a href='mailto:support@ezbudget.app'>support@ezbudget.app</a></p></div></div>";

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
