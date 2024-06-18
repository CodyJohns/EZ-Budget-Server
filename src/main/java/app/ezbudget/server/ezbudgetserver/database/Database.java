package app.ezbudget.server.ezbudgetserver.database;

public interface Database<T> {

    /**
     * Get the database's current connection or handle.
     * 
     * @return Database handle or connection
     */
    T getHandle();

    /**
     * Forceably close the connection with the database. 
     * Should be used only when necessary.
     */
    void close();

    /**
     * Connect to the database. 
     * Calling this function may create more overhead 
     * and should only be used when necessary.
     */
    void connect();
}
