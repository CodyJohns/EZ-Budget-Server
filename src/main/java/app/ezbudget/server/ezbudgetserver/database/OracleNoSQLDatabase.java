package app.ezbudget.server.ezbudgetserver.database;

import oracle.nosql.driver.*;
import oracle.nosql.driver.iam.SignatureProvider;

public class OracleNoSQLDatabase implements Database<NoSQLHandle> {

    private AuthorizationProvider ap;
    private NoSQLHandleConfig config;
    private NoSQLHandle handle;
    private final String COMPARTMENT = "EZ_Budget";

    public OracleNoSQLDatabase() {
        ap = SignatureProvider.createWithResourcePrincipal();

        config = new NoSQLHandleConfig(Region.US_PHOENIX_1, ap)
                .setAuthorizationProvider(ap)
                .setDefaultCompartment(COMPARTMENT);
    }

    public NoSQLHandle getHandle() {
        return handle;
    }

    public void connect() {
        if (handle == null)
            handle = NoSQLHandleFactory.createNoSQLHandle(config);
    }

    public void close() {
        handle.close();
    }
}
