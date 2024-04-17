package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.BookedUp.domain.ConfirmationToken;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.TokenDTO;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.IEmailService;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.IRegistrationService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService implements IRegistrationService {
    @Autowired
    private UserService service;
    @Autowired
    private ConfirmationTokenService confirmationTokenService;
    @Autowired
    private IEmailService emailSender;

    @Transactional
    public String register(User user) {

        service.save(user);

        ConfirmationToken token = generateToken(user);
        confirmationTokenService.saveConfirmationToken(token);
        String link = "http://localhost:8080/api/registration?token=" + token.getToken();

        emailSender.send(
                user.getEmail(),
                buildEmail(user.getFirstName(), link));

        return token.getToken();
    }

    @Transactional
    public TokenDTO androidRegister(User user) {

        service.save(user);

        ConfirmationToken token = generateToken(user);
        confirmationTokenService.saveConfirmationToken(token);
        String link = "http://192.168.0.2:8080/api/registration?token=" + token.getToken();

        emailSender.send(
                user.getEmail(),
                buildEmail(user.getFirstName(), link));

        return new TokenDTO(token.getToken());
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
//        TODO user is verified

        User user = confirmationToken.getUser();
        user.setActive(true);
        user.setVerified(true);

        service.save(user);

        return "confirmed";
    }


    public ConfirmationToken generateToken(User user) {
        return new ConfirmationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusDays(1),
                user
        );
    }

    private String buildEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: 'Adamina', sans-serif;\n" +
                "      background-color: #ffffff;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 20px auto;\n" +
                "      padding: 20px;\n" +
                "      background-color: #fff;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "    }\n" +
                "    h1 {\n" +
                "      color: #9e8d5f;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    p {\n" +
                "      color: #333;\n" +
                "      line-height: 1.6;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .cta-button {\n" +
                "      display: inline-block;\n" +
                "      padding: 10px 20px;\n" +
                "      background-color: #9e8d5f;\n" +
                "      color: #fff;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 5px;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      margin-top: 20px;\n" +
                "      text-align: center;\n" +
                "      color: #777;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <h1>Welcome to BookedUp!</h1>\n" +
                "    <p>Hello " + name + ",</p>\n" +
                "    <p>We're thrilled to have you on board. Your journey with BookedUp begins now!</p>\n" +
                "    <p>This warm welcome comes to you from Vesna, Ana, and Dušica!</p>\n" +
                "    <p>To activate your account, simply click the button below:</p>\n" +
                "    <p style=\"text-align: center;\">\n" +
                "      <a href=\"" + link + "\" class=\"cta-button\">Activate Account</a>\n" +
                "    </p>\n" +
                "    <p>This link will expire in 24 hours.</p>\n" +
                "    <p>If you have any questions or need assistance, feel free to reach out to our support team.</p>\n" +
                "    <div class=\"footer\">\n" +
                "      <p>Best regards,<br> The BookedUp Team (Vesna, Ana, Dušica)</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
    }
}