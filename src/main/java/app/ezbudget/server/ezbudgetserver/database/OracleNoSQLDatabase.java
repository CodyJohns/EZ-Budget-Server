package app.ezbudget.server.ezbudgetserver.database;

import java.io.IOException;

import oracle.nosql.driver.*;
import oracle.nosql.driver.iam.SignatureProvider;

public class OracleNoSQLDatabase implements Database<NoSQLHandle> {

    private AuthorizationProvider ap;
    private NoSQLHandleConfig config;
    private NoSQLHandle handle;

    public OracleNoSQLDatabase() throws IOException {
        if (System.getenv("DEV").equals("true")) {
            ap = new SignatureProvider();
        } else {
            ap = SignatureProvider.createWithResourcePrincipal();
        }

        config = new NoSQLHandleConfig(Region.US_PHOENIX_1, ap)
                .setAuthorizationProvider(ap)
                .setDefaultCompartment(System.getenv("COMPARTMENT_OCID"));
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
