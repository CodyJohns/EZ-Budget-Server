package app.ezbudget.server.ezbudgetserver.model;

import app.ezbudget.server.ezbudgetserver.util.Utilities;

public class Password {

    public String password;

    public Password() {}

    public Password(String raw_pass) {
        this.password = Utilities.getEncoder().encode(raw_pass);
    }

    public boolean compare(String raw_pass) {
        return Utilities.getEncoder().matches(raw_pass, password);
    }

    public void hashPassword(String raw_pass) {
        this.password = Utilities.getEncoder().encode(raw_pass);
    }

    public String getHashedPassword() {
        return this.password;
    }

    public void setHashedPassword(String password) {
        this.password = password;
    }
}
