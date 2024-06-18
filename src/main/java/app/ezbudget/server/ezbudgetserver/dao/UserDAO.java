package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.model.User;

public interface UserDAO {

    /**
     * Get user object by authtoken.
     * 
     * @param authtoken
     * @return User
     */
    User getUserByAuthtoken(String authtoken);

    /**
     * Get user object by password change code.
     * 
     * @param code
     * @return User
     */
    User getUserByPasswordChangeCode(String code);

    /**
     * Get user object by account verify code.
     * 
     * @param code
     * @return User
     */
    User getUserByVerifyCode(String code);

    /**
     * Get user object by username.
     * 
     * @param username
     * @return User
     */
    User getUserByUsername(String username);

    /**
     * Get user object by email.
     * 
     * @param email
     * @return User
     */
    User getUserByEmail(String email);

    /**
     * Create a new user.
     * 
     * @param user
     */
    void createNew(User user);

    /**
     * Save or update a user.
     * 
     * @param user
     */
    void save(User user);

    /**
     * Check if user exists.
     * 
     * @param id
     * @return True if exists
     */
    boolean userExists(String id);

    /**
     * Delete user from database.
     * 
     * @param user
     */
    void deleteUser(User user);
}
