package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.exceptions.AccessDeniedException;
import app.ezbudget.server.ezbudgetserver.exceptions.SendMailException;
import app.ezbudget.server.ezbudgetserver.model.JointAccountData;
import app.ezbudget.server.ezbudgetserver.model.Password;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.util.Utilities;

import java.util.HashMap;
import java.util.Map;

public class AccountService {

    private DAOFactory factory;

    public AccountService(DAOFactory factory) {
        this.factory = factory;
    }

    public HTTPResponse initiatePasswordChange(String identifier) throws SendMailException {

        User user;

        if(identifier.contains("@") && identifier.contains("."))
            user = this.factory.getUserDAO().getUserByEmail(identifier);
        else
            user = this.factory.getUserDAO().getUserByUsername(identifier);

        String email = user.getEmail();

        String code = Utilities.generateCode(8);

        user.setPasswordChangeCode(code);
        user.setAwaitingPasswordChange(true);

        this.factory.getUserDAO().save(user);

        //send email
        boolean result = this.factory.getMailer().sendMail(
                "Forgotten Password",
                "Visit the link to reset your password: <a href='https://ezbudget.app/password/change/"+ code + "'>https://ezbudget.app/password/change/" + code + "</a>",
                "Visit the link to reset your password: https://ezbudget.app/password/change/" + code,
                email
        );

        if(!result)
            throw new SendMailException("Failed to send email");

        return new HTTPResponse(200, "Password change request sent to your email.  Can't see it? Make sure to check your spam folder as well.");
    }

    public HTTPResponse authPasswordChange(String code) {
        this.factory.getUserDAO().getUserByPasswordChangeCode(code);

        return new HTTPResponse(200, "Ok");
    }

    public HTTPResponse completePasswordChange(String code, String new_pass) {

        User user = this.factory.getUserDAO().getUserByPasswordChangeCode(code);

        Password password = user.getPassword();
        password.hashPassword(new_pass);

        user.setPasswordChangeCode("1");
        user.setAwaitingPasswordChange(false);

        this.factory.getUserDAO().save(user);

        return new HTTPResponse(200, "Password changed successfully. Please try to login again using the app.");
    }

    public HTTPResponse updatePassword(String authtoken, String old_pass, String new_pass) throws AccessDeniedException, SendMailException {

        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        Password password = user.getPassword();

        if(!password.compare(old_pass))
            throw new AccessDeniedException("Error: Current password is incorrect.");

        password.hashPassword(new_pass);

        this.factory.getUserDAO().save(user);

        boolean result = this.factory.getMailer().sendMail(
                "Your account password has been changed",
                user.getUsername() + ", your account password has been changed.<br /><br /> If this action was not performed by you, please contact us at <a href='mailto:support@ezbudget.app'>support@ezbudget.app</a>",
                user.getUsername() + ", your account password has been changed. If this action was not performed by you, please contact us at support@ezbudget.app",
                user.getEmail()
        );

        if(!result)
            throw new SendMailException("Failed to send email");

        return new HTTPResponse(200, "Password updated.");
    }

    /**
     * 
     * @deprecated notifications happen solely on the front-end currently.
     */
    @Deprecated
    public HTTPResponse getEmailsEnabled(String authtoken) {

        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        boolean emailsEnabled = user.isEmailReminderEnabled();

        Map<String, Boolean> result = new HashMap<>();

        result.put("emails", emailsEnabled);

        return new HTTPResponse(200, "Ok", result);
    }

    /**
     * 
     * @deprecated notifications happen solely on the front-end currently.
     */
    @Deprecated
    public HTTPResponse setEmailsEnabled(String authtoken, boolean enabled) {

        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        user.setEmailRemindersEnabled(enabled);

        this.factory.getUserDAO().save(user);

        return new HTTPResponse(200, "Updated preferences.");
    }

    public HTTPResponse getJointAccountHolders(String authtoken) {

        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        return new HTTPResponse(200, "Ok", user.getJointData().getAllowedJointUsers());
    }

    public HTTPResponse getJointAccountStatus(String authtoken) {
        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        Map<String, Boolean> data = new HashMap<>();

        data.put("isHost", user.getJointData().getAllowedJointUsers().size() > 0);
        data.put("isJointUser", user.getJointData().isJointWith());

        return new HTTPResponse(200, "Ok", data);
    }

    public HTTPResponse getSentJointAccountRequests(String authtoken) {

        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        return new HTTPResponse(200, "Ok", user.getJointData().getSentJointRequests().size());
    }

    public HTTPResponse getPendingJointAccountRequests(String authtoken) {

        User user = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        return new HTTPResponse(200, "Ok", user.getJointData().getJointAccountRequests());
    }

    public HTTPResponse cancelSentJointAccountRequests(String authtoken) {

        User requestorUser = this.factory.getUserDAO().getUserByAuthtoken(authtoken);

        Map<String, String> requests = requestorUser.getJointData().getSentJointRequests();

        //there may just be one, but just to future-proof it
        for(String key : requests.keySet()) {
            User hostUser = this.factory.getUserDAO().getUserByAuthtoken(key);

            hostUser.getJointData().getJointAccountRequests().remove(requestorUser.getAuthtoken());

            this.factory.getUserDAO().save(hostUser);
        }

        requestorUser.getJointData().getSentJointRequests().clear();

        this.factory.getUserDAO().save(requestorUser);

        return new HTTPResponse(200, "OK");
    }

    public HTTPResponse acceptJointAccountHolderRequest(String hostAuthtoken, String jointAuthtoken) {

        User hostUser = this.factory.getUserDAO().getUserByAuthtoken(hostAuthtoken);
        User jointUser = this.factory.getUserDAO().getUserByAuthtoken(jointAuthtoken);

        JointAccountData hostData = hostUser.getJointData();
        JointAccountData jointUserData = jointUser.getJointData();

        //if request for joint account membership exists...
        if(hostData.getJointAccountRequests().containsKey(jointUser.getAuthtoken())) {

            //add the joint requestor's authtoken and username as an entry in the host's joint data
            hostData.getAllowedJointUsers().put(jointUser.getAuthtoken(), jointUser.getUsername());
            hostData.getJointAccountRequests().remove(jointUser.getAuthtoken());
            //set the joint user's host id to the host's authtoken. They
            //will never see the host's authtoken.
            jointUserData.setJointHostID(hostUser.getAuthtoken());
            jointUserData.getSentJointRequests().remove(hostUser.getAuthtoken());

            this.factory.getUserDAO().save(hostUser);
            this.factory.getUserDAO().save(jointUser);
        } else {
            return new HTTPResponse(200, "Unable to add: user didn't send request.");
        }

        return new HTTPResponse(200, "Joint account holder added.");
    }

    public HTTPResponse removeJointAccountHolder_JointPerspective(String jointAuthtoken) {
        User jointUser = this.factory.getUserDAO().getUserByAuthtoken(jointAuthtoken);

        if(!jointUser.getJointData().isJointWith())
            return new HTTPResponse(200, "Not joint with any user.");

        User hostUser = this.factory.getUserDAO().getUserByAuthtoken(jointUser.getJointData().getJointHostID());

        JointAccountData hostData = hostUser.getJointData();
        JointAccountData requestorData = jointUser.getJointData();

        hostData.getAllowedJointUsers().remove(jointUser.getAuthtoken());
        requestorData.setJointHostID(null);

        this.factory.getUserDAO().save(hostUser);
        this.factory.getUserDAO().save(jointUser);

        return new HTTPResponse(200, "Joint account user access eliminated.");
    }

    public HTTPResponse removeJointAccountHolder_HostPerspective(String hostAuthtoken, String jointAuthtoken) {
        User hostUser = this.factory.getUserDAO().getUserByAuthtoken(hostAuthtoken);

        if(hostUser.getJointData().getAllowedJointUsers().size() == 0)
            return new HTTPResponse(200, "No joint users");

        User jointUser = this.factory.getUserDAO().getUserByAuthtoken(jointAuthtoken);

        JointAccountData hostData = hostUser.getJointData();
        JointAccountData requestorData = jointUser.getJointData();

        hostData.getAllowedJointUsers().remove(jointUser.getAuthtoken());
        requestorData.setJointHostID(null);

        this.factory.getUserDAO().save(hostUser);
        this.factory.getUserDAO().save(jointUser);

        return new HTTPResponse(200, "Joint account user access eliminated.");
    }

    public HTTPResponse sendJointAccountHolderRequest(String jointAuthtoken, String hostUsername) {

        User jointUser = this.factory.getUserDAO().getUserByAuthtoken(jointAuthtoken);
        User hostUser = this.factory.getUserDAO().getUserByUsername(hostUsername);

        JointAccountData hostData = hostUser.getJointData();
        JointAccountData requestorData = jointUser.getJointData();

        hostData.getJointAccountRequests().put(jointUser.getAuthtoken(), jointUser.getUsername());
        requestorData.getSentJointRequests().put(hostUser.getAuthtoken(), hostUser.getUsername());

        this.factory.getUserDAO().save(hostUser);
        this.factory.getUserDAO().save(jointUser);

        return new HTTPResponse(200, "Joint account request sent.");
    }

    public HTTPResponse rejectJointAccountHolderRequest(String hostAuthtoken, String jointAuthtoken) {

        User jointUser = this.factory.getUserDAO().getUserByAuthtoken(jointAuthtoken);
        User hostUser = this.factory.getUserDAO().getUserByAuthtoken(hostAuthtoken);

        JointAccountData hostData = hostUser.getJointData();
        JointAccountData requestorData = jointUser.getJointData();

        hostData.getJointAccountRequests().remove(jointUser.getAuthtoken());
        requestorData.getSentJointRequests().remove(hostUser.getAuthtoken());

        this.factory.getUserDAO().save(hostUser);
        this.factory.getUserDAO().save(jointUser);

        return new HTTPResponse(200, "Joint account request rejected.");
    }
}
