package app.ezbudget.server.ezbudgetserver.model;

/**
 * When the user requests a list of all their entries we 
 * want to only send the entry id and timestamp instead of the entire object.
 */
public class BasicEntry {

    public String id;
    public String timestamp;

    public BasicEntry(String id, String timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() { return timestamp; }
}
