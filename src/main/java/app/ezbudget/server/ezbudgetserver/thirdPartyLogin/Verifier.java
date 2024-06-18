package app.ezbudget.server.ezbudgetserver.thirdPartyLogin;

public interface Verifier<T> {

    /**
     * Validate login token for a 3rd party login.
     * 
     * @param credential
     * @return Token object
     */
    T getToken(String credential);
}

