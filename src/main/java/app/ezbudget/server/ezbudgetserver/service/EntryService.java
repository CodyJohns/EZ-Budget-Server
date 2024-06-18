package app.ezbudget.server.ezbudgetserver.service;

import app.ezbudget.server.ezbudgetserver.dao.DAOFactory;
import app.ezbudget.server.ezbudgetserver.model.BasicEntry;
import app.ezbudget.server.ezbudgetserver.model.BasicEntryWrapper;
import app.ezbudget.server.ezbudgetserver.model.Entry;
import app.ezbudget.server.ezbudgetserver.model.User;
import app.ezbudget.server.ezbudgetserver.util.HTTPResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryService extends JointService {

    private final int PAGE_SIZE = 12;

    public EntryService(DAOFactory factory) {
        super(factory);
    }

    /**
     * Get all entries of a user. Entries will contain basic information such as entry id and timestamp.
     * 
     * @param authtoken
     * @return HTTPResponse object
     */
    public HTTPResponse getEntries(String authtoken) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<Entry> entries = this.factory.getEntryDAO().getAllEntries(user.getAuthtoken());

        List<BasicEntry> data = new ArrayList<>();

        for (Entry entry : entries)
            data.add(entry.getBasicEntry());

        BasicEntryWrapper wrapper = new BasicEntryWrapper(data, false);

        return new HTTPResponse(200, "Ok", wrapper);
    }

    public HTTPResponse getEntriesPaged(String authtoken, int page)  {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        List<Entry> entries = this.factory.getEntryDAO().getSpecificNumberOfEntries(user.getAuthtoken(), PAGE_SIZE * page);

        List<BasicEntry> data = new ArrayList<>();

        for (Entry entry : entries)
            data.add(entry.getBasicEntry());

        boolean hasMore = data.size() < user.getNumEntries();

        BasicEntryWrapper wrapper = new BasicEntryWrapper(data, hasMore);

        return new HTTPResponse(200, "Ok", wrapper);
    }

    /**
     * Get a single entry.
     * 
     * @param authtoken
     * @param id
     * @return HTTPResponse object
     */
    public HTTPResponse getEntry(String authtoken, String id) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Entry entry = this.factory.getEntryDAO().getEntry(user.getAuthtoken(), id);

        List<Entry> entries = new ArrayList<>();

        entries.add(entry);

        return new HTTPResponse(200, "Ok", entries);
    }

    public HTTPResponse addEntry(String authtoken, Entry entry) throws ParseException {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        user.setNumEntries(user.getNumEntries() + 1);

        this.factory.getUserDAO().save(user);
        this.factory.getEntryDAO().addEntry(user.getAuthtoken(), entry);

        //reset user's monthly purchases
        this.factory.getPurchaseDAO().delete(user.getAuthtoken());

        return new HTTPResponse(200, "Entry added");
    }

    public HTTPResponse updateEntry(String authtoken, Entry entry) throws ParseException {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        this.factory.getEntryDAO().save(user.getAuthtoken(), entry);

        return new HTTPResponse(200, "Entry updated");
    }

    public HTTPResponse deleteEntry(String authtoken, String id) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Entry entry = this.factory.getEntryDAO().getEntry(user.getAuthtoken(), id);

        user.setNumEntries(user.getNumEntries() - 1);

        this.factory.getEntryDAO().deleteEntry(user.getAuthtoken(), entry.getId());
        this.factory.getUserDAO().save(user);

        return new HTTPResponse(200, "Entry deleted");
    }

    public HTTPResponse markEntry(String authtoken, String id, boolean marked) throws ParseException {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Entry entry = this.factory.getEntryDAO().getEntry(user.getAuthtoken(), id);

        entry.setMarked(marked);

        this.factory.getEntryDAO().save(user.getAuthtoken(), entry);

        return new HTTPResponse(200, "Entry marked");
    }

    public HTTPResponse getMarked(String authtoken, String id) {

        User user = getTargetUser(this.factory.getUserDAO().getUserByAuthtoken(authtoken));

        Entry entry = this.factory.getEntryDAO().getEntry(user.getAuthtoken(), id);

        boolean isMarked = entry.isMarked();

        Map<String, Boolean> data = new HashMap<>();

        data.put("marked", isMarked);

        return new HTTPResponse(200, "Ok", data);
    }
}
