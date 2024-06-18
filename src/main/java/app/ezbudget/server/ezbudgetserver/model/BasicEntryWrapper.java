package app.ezbudget.server.ezbudgetserver.model;

import java.util.List;

/**
 * Used when serializing entries for a pagination in the app.
 */
public class BasicEntryWrapper {

    public List<BasicEntry> entries;
    public boolean hasMore;

    public BasicEntryWrapper(List<BasicEntry> entries, boolean hasMore) {
        this.entries = entries;
        this.hasMore = hasMore;
    }
}
