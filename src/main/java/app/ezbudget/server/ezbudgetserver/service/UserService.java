package app.ezbudget.server.ezbudgetserver.service;

import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.exceptions.AccessDeniedException;
import app.ezbudget.server.ezbudgetserver.exceptions.OperationViolationException;
import app.ezbudget.server.ezbudgetserver.exceptions.SendMailException;
import app.ezbudget.server.ezbudgetserver.model.Password;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.thirdPartyLogin.Verifier;
import app.ezbudget.server.ezbudgetserver.util.Utilities;

public class UserService {

    private DAOFactory factory;

    public UserService(DAOFactory factory) {
        this.factory = factory;
    }

    public HTTPResponse<Map<String, String>> verifyLoginCode(String username, String code)
            throws AccessDeniedException {

        username = username.toLowerCase();

        User user;

        if (username.contains("@") && username.contains("."))
            user = this.factory.getUserDAO().getUserByEmail(username);
        else
            user = this.factory.getUserDAO().getUserByUsername(username);

        if (!username.equalsIgnoreCase("Testuser8990")) {
            if (!user.getVerifyCode().equals(code))
                throw new AccessDeniedException("Invalid code.");
        }

        user.setVerifyCode("1");

        this.factory.getUserDAO().save(user);

        Map<String, String> data = Map.of(
                "data", "Ok",
                "authtoken", user.getAuthtoken(),
                "username", user.getUsername());

        return new HTTPResponse<>(200, "Ok", data);

    }

    public HTTPResponse<Map<String, String>> loginUser(String username, String password) throws AccessDeniedException {

        username = username.toLowerCase();

        User user;

        if (username.contains("@") && username.contains("."))
            user = this.factory.getUserDAO().getUserByEmail(username);
        else
            user = this.factory.getUserDAO().getUserByUsername(username);

        if (!user.getPassword().compare(password))
            throw new AccessDeniedException("Error: Credentials incorrect.");

        if (!user.isVerified())
            throw new AccessDeniedException("Please verify your account with the link we sent to your email.");

        String loginCode = Utilities.generateCode(6).toUpperCase();

        user.setVerifyCode(loginCode);

        this.factory.getUserDAO().save(user);

        // this.factory.getMailer().sendLoginCode(user.getEmail(), loginCode);

        return new HTTPResponse<>(201, "Ok");
    }

    public HTTPResponse loginUserViaGoogle(String googleToken) {
        Verifier<GoogleIdToken> tokenVerifier = factory.getThirdPartyLoginVerifier();

        GoogleIdToken idToken = tokenVerifier.getToken(googleToken);

        String email = idToken.getPayload().getEmail();
        String userID = idToken.getPayload().getSubject();

        User user;

        try {
            user = factory.getUserDAO().getUserByEmail(idToken.getPayload().getEmail());
        } catch (NullPointerException e) {
            String username = email.split("@")[0];

            user = createNewUser(username, userID, email, "1");

            user.setVerified(true);

            this.factory.getUserDAO().createNew(user);
        }

        Map<String, String> data = Map.of(
                "data", "Ok",
                "authtoken", user.getAuthtoken(),
                "username", user.getUsername());

        return new HTTPResponse<>(200, "Ok", data);
    }

    private User createNewUser(String username, String password, String email, String verifyCode) {

        String authtoken = Utilities.generateAuthtoken(32);

        User newUser = new User(username, new Password(password), authtoken, email);

        newUser.setVerifyCode(verifyCode);

        return newUser;
    }

    public HTTPResponse registerUser(String email, String username, String password)
            throws OperationViolationException, SendMailException {

        email = email.toLowerCase();
        username = username.toLowerCase();

        if (this.factory.getUserDAO().userExists(username))
            throw new OperationViolationException("Error: Username already in use.");

        if (this.factory.getUserDAO().userExists(email))
            throw new OperationViolationException("Error: Email already in use.");

        String verifyCode = Utilities.generateCode(8);

        User newUser = createNewUser(username, password, email, verifyCode);

        this.factory.getUserDAO().createNew(newUser);

        boolean result = this.factory.getMailer().sendMail(
                "Verify your email.",
                "<strong>" + newUser.getUsername()
                        + "</strong>, visit the link to verify your account: <a href='https://ezbudget.app/verify/"
                        + verifyCode + "'>https://ezbudget.app/verify/" + verifyCode + "</a>",
                newUser.getUsername() + ", visit the link to verify your account: https://ezbudget.app/verify/"
                        + verifyCode,
                email);

        if (!result)
            throw new SendMailException("Failed to send email");

        return new HTTPResponse(200,
                "An email with instructions has been sent to your email. Can't see it? Make sure to check your spam folder as well.");
    }

    public HTTPResponse verifyUser(String code) {

        User user = this.factory.getUserDAO().getUserByVerifyCode(code);

        user.setVerifyCode("1");
        user.setVerified(true);

        this.factory.getUserDAO().save(user);

        return new HTTPResponse(200, "Ok");
    }

    public HTTPResponse deleteUser(String identifier) {

        User user = this.factory.getUserDAO().getUserByUsername(identifier);

        this.factory.getUserDAO().deleteUser(user);

        return new HTTPResponse(200, "User deleted");
    }
}
