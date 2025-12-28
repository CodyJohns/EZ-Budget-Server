package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.model.*;
import com.google.gson.Gson;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.*;
import oracle.nosql.driver.values.MapValue;
import oracle.nosql.driver.values.StringValue;

public class OracleUserDAO implements UserDAO {

    private Database<NoSQLHandle> database;
    private Gson gson;
    private final String TABLE_NAME = "users";
    private final String EMAIL = "email";
    private final String USERNAME = "username";
    private final String AUTHTOKEN = "authtoken";
    private final String VERIFIED = "verify";
    private final String PASSWORD_CHANGE = "password_change";
    private final String USER_DATA = "user_data";

    public OracleUserDAO(Database database) {
        this.database = database;
        this.gson = new Gson();
    }

    @Override
    public User getUserByAuthtoken(String authtoken) {

        MapValue key = new MapValue().put(AUTHTOKEN, authtoken);

        GetRequest request = new GetRequest().setKey(key).setTableName(TABLE_NAME);

        GetResult result = database.getHandle().get(request);

        if (result.getValue() == null)
            throw new NullPointerException("User does not exist");

        MapValue row = result.getValue();

        return loadUser(row);
    }

    @Override
    public User getUserByPasswordChangeCode(String code) {
        return getUserByColumn(PASSWORD_CHANGE, code);
    }

    @Override
    public User getUserByVerifyCode(String code) {
        return getUserByColumn(VERIFIED, code);
    }

    @Override
    public User getUserByUsername(String username) {
        return getUserByColumn(USERNAME, username.toLowerCase());
    }

    @Override
    public User getUserByEmail(String email) {
        return getUserByColumn(EMAIL, email.toLowerCase());
    }

    private User getUserByColumn(String field, String value) {

        String query = "DECLARE $iden_value STRING; " +
                "SELECT * FROM " + TABLE_NAME + " WHERE " + field + " = $iden_value";

        PrepareRequest prepReq = new PrepareRequest().setStatement(query);

        PrepareResult prepRes = database.getHandle().prepare(prepReq);

        prepRes.getPreparedStatement().setVariable("$iden_value", new StringValue(value));

        QueryRequest request = new QueryRequest().setPreparedStatement(prepRes);

        QueryResult result = database.getHandle().query(request);

        if (result.getResults().size() == 0)
            throw new NullPointerException("User does not exist");

        MapValue row = result.getResults().get(0);

        return loadUser(row);
    }

    private User loadUser(MapValue row) {
        User user = gson.fromJson(row.getString(USER_DATA), User.class);
        user.subscription_type = user.subscription_type == null ? User.SubscriptionType.FREE : user.subscription_type;

        if (user.getJointData() == null)
            user.jointData = new JointAccountData();

        return user;
    }

    @Override
    public void createNew(User user) {
        user.setAwaitingPasswordChange(false);
        user.setEmailRemindersEnabled(true);
        user.setVerified(false);

        save(user);
    }

    @Override
    public void save(User user) {

        MapValue value = new MapValue()
                .put(EMAIL, user.getEmail())
                .put(USERNAME, user.getUsername())
                .put(AUTHTOKEN, user.getAuthtoken())
                .put(VERIFIED, user.isVerified() ? "1" : user.getVerifyCode())
                .put(PASSWORD_CHANGE, user.awaitingPasswordChange() ? user.getPasswordChangeCode() : "1")
                .put(USER_DATA, gson.toJson(user));

        PutRequest putRequest = new PutRequest().setValue(value).setTableName(TABLE_NAME);

        database.getHandle().put(putRequest);
    }

    @Override
    public boolean userExists(String id) {

        String query = "DECLARE $iden_value STRING; " +
                "SELECT username FROM " + TABLE_NAME + " WHERE " + USERNAME + " = $iden_value OR " + EMAIL
                + " = $iden_value";

        PrepareRequest prepReq = new PrepareRequest().setStatement(query);

        PrepareResult prepRes = database.getHandle().prepare(prepReq);

        prepRes.getPreparedStatement().setVariable("$iden_value", new StringValue(id));

        QueryRequest request = new QueryRequest().setPreparedStatement(prepRes);

        QueryResult result = database.getHandle().query(request);

        if (result.getResults().size() > 0)
            return true;

        return false;
    }

    @Override
    public void deleteUser(User user) {
        MapValue key = new MapValue().put(AUTHTOKEN, user.getAuthtoken());

        DeleteRequest delRequest = new DeleteRequest().setTableName(TABLE_NAME).setKey(key);

        database.getHandle().delete(delRequest);
    }
}
