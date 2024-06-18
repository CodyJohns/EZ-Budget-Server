package app.ezbudget.server.ezbudgetserver.database;

import oracle.nosql.driver.*;
import oracle.nosql.driver.iam.SignatureProvider;

import java.io.IOException;

public class OracleNoSQLDatabase implements Database<NoSQLHandle> {

    private AuthorizationProvider ap;
    private NoSQLHandleConfig config;
    private NoSQLHandle handle;
    private final String COMPARTMENT = "EZ_Budget";

    public OracleNoSQLDatabase() throws IOException {
        ap = new SignatureProvider("./oci/config", "DEFAULT");

        config = new NoSQLHandleConfig(Region.US_PHOENIX_1, ap);
        config.setAuthorizationProvider(ap);
        config.setDefaultCompartment(COMPARTMENT);
    }

    public NoSQLHandle getHandle() {
        return handle;
    }

    public void connect() {
        //should be created rarely because uses a lot of overhead to create and setup handles
        if(handle == null)
            handle = NoSQLHandleFactory.createNoSQLHandle(config);
    }

    public void close() { handle.close(); }
}
