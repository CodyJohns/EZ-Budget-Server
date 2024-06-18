package app.ezbudget.server.ezbudgetserver.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String username;
    public Password password;
    public String authtoken;
    public String email;
    public boolean verify;
    public String verify_code;
    public boolean password_change;
    public String password_change_code;
    public boolean email_reminder;
    public int entries;
    public JointAccountData jointData;
    public List<VariableExpense> variable_presets;
    public List<CalculatedExpense> calculated_presets;

    //for testing users
    public User(String username, String password, String authtoken, String email) {
        setFields(username, new Password(password), authtoken, email);
    }

    //for creating new users from app
    public User(String username, Password password, String authtoken, String email) {
        setFields(username, password, authtoken, email);
    }

    private void setFields(String username, Password password, String authtoken, String email) {
        this.username = username;
        this.password = password;
        this.authtoken = authtoken;
        this.email = email;
        this.variable_presets = new ArrayList<>();
        this.calculated_presets = new ArrayList<>();
        this.entries = 0;
        this.jointData = new JointAccountData();
    }

    public String getUsername() {
        return username;
    }

    public Password getPassword() {
        return password;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verify;
    }

    public String getVerifyCode() {
        return verify_code;
    }

    public boolean awaitingPasswordChange() {
        return password_change;
    }

    public String getPasswordChangeCode() {
        return password_change_code;
    }

    public int getNumEntries() { return entries; }

    public void setNumEntries(int entries) { this.entries = entries; }

    public boolean isEmailReminderEnabled() {
        return email_reminder;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVerified(boolean verify) {
        this.verify = verify;
    }

    public void setVerifyCode(String verify_code) {
        this.verify_code = verify_code;
    }

    public void setAwaitingPasswordChange(boolean password_change) {
        this.password_change = password_change;
    }

    public void setPasswordChangeCode(String password_change_code) {
        this.password_change_code = password_change_code;
    }

    public void setEmailRemindersEnabled(boolean email_reminder) {
        this.email_reminder = email_reminder;
    }

    public List<VariableExpense> getVariablePresets() {
        return variable_presets;
    }

    public List<CalculatedExpense> getCalculatedPresets() {
        return calculated_presets;
    }

    public void setVariablePresets(List<VariableExpense> variable_presets) {
        this.variable_presets = variable_presets;
    }

    public void setCalculatedPresets(List<CalculatedExpense> calculated_presets) {
        this.calculated_presets = calculated_presets;
    }

    public JointAccountData getJointData() { return jointData; }

    @Override
    public String toString() {
        return "{ username: " + username + ", email: " + email +
                ", authtoken: " + authtoken + ", emails_enabled: " + email_reminder + " }";
    }
}
