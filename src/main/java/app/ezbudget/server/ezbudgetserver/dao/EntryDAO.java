package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.model.Entry;

import java.text.ParseException;
import java.util.List;

public interface EntryDAO {

    /**
     * Get all the entries created by the user.
     * 
     * This will not page results.
     * 
     * @param authtoken
     * @return List<Entry> entries
     */
    List<Entry> getAllEntries(String authtoken);

    /**
     * Get up to a specified amount of entries for the user.
     * 
     * @param authtoken
     * @param count
     * @return List<Entry> entries
     */
    List<Entry> getSpecificNumberOfEntries(String authtoken, int count);

    /**
     * Get a single user's entry by its ID.
     * 
     * @param authtoken
     * @param id
     * @return Entry
     */
    Entry getEntry(String authtoken, String id);

    /**
     * Add an entry for a user.
     * 
     * @param authtoken
     * @param entry
     * @throws ParseException
     */
    void addEntry(String authtoken, Entry entry) throws ParseException;

    /**
     * Delete an entry.
     * 
     * @param authtoken
     * @param id
     */
    void deleteEntry(String authtoken, String id);

    /**
     * Save or update an entry.
     * 
     * @param authtoken
     * @param entry
     * @throws ParseException
     */
    void save(String authtoken, Entry entry) throws ParseException;
}
