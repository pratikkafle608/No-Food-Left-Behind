import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtility {

    public static void sendEmail(String recipient, String subject, String messageText) throws MessagingException {
        final String fromEmail = "nofoodlb@gmail.com"; //my email id
        final String password = "capstone2025";    //my email password for nofoodlb@gmail.com

        // Configure mail properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create mail session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Create and send message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        msg.setSubject(subject);
        msg.setText(messageText);

        Transport.send(msg);
        System.out.println("✅ Email sent to: " + recipient);
    }
}
