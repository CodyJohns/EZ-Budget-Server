package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.model.Entry;
import app.ezbudget.server.ezbudgetserver.util.Utilities;
import com.google.gson.Gson;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.*;
import oracle.nosql.driver.values.IntegerValue;
import oracle.nosql.driver.values.MapValue;
import oracle.nosql.driver.values.StringValue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class OracleEntryDAO implements EntryDAO {

    private Database<NoSQLHandle> database;
    private Gson gson;
    private final String TABLE_NAME = "entries";
    private final String AUTHTOKEN = "authtoken";
    private final String ID = "id";
    private final String JSON = "json";
    private final String TIMESTAMP = "timestamp";

    public OracleEntryDAO(Database database) {
        this.database = database;
        this.gson = new Gson();
    }

    @Override
    public List<Entry> getAllEntries(String authtoken) {

        String query = "DECLARE $authtoken STRING; " +
                       "SELECT * FROM " + TABLE_NAME + " WHERE " + AUTHTOKEN + " = $authtoken ORDER BY " + TIMESTAMP + " DESC";

        PrepareRequest prepReq = new PrepareRequest().setStatement(query);

        PrepareResult prepRes = database.getHandle().prepare(prepReq);
        
        prepRes.getPreparedStatement().setVariable("$authtoken", new StringValue(authtoken));

        QueryRequest request = new QueryRequest().setPreparedStatement(prepRes);

        QueryResult result = database.getHandle().query(request);

        List<Entry> entries = new ArrayList<>();

        for (MapValue row : result.getResults())
            entries.add(gson.fromJson(row.getString(JSON), Entry.class));

        return entries;
    }

    @Override
    public List<Entry> getSpecificNumberOfEntries(String authtoken, int count) {

        String query = "DECLARE $authtoken STRING; " +
                       "SELECT * FROM " + TABLE_NAME + " WHERE " + AUTHTOKEN + " = $authtoken ORDER BY " + TIMESTAMP + " DESC LIMIT " + count;

        PrepareRequest prepReq = new PrepareRequest().setStatement(query);

        PrepareResult prepRes = database.getHandle().prepare(prepReq);
        
        prepRes.getPreparedStatement().setVariable("$authtoken", new StringValue(authtoken));

        QueryRequest request = new QueryRequest().setPreparedStatement(prepRes);

        QueryResult result = database.getHandle().query(request);

        List<Entry> entries = new ArrayList<>();

        for(MapValue row : result.getResults())
            entries.add(gson.fromJson(row.getString(JSON), Entry.class));

        return entries;
    }

    @Override
    public Entry getEntry(String authtoken, String id) {

        MapValue key = new MapValue().put(ID, id).put(AUTHTOKEN, authtoken);

        GetRequest request = new GetRequest().setKey(key).setTableName(TABLE_NAME);

        GetResult result = database.getHandle().get(request);

        if(result.getValue() == null)
            throw new NullPointerException("Entry does not exist");

        return gson.fromJson(result.getValue().getString(JSON), Entry.class);
    }

    @Override
    public void addEntry(String authtoken, Entry entry) throws ParseException {
        entry.setId(Utilities.generateAuthtoken(32));
        save(authtoken, entry);
    }

    @Override
    public void deleteEntry(String authtoken, String id) {

        MapValue key = new MapValue().put(ID, id).put(AUTHTOKEN, authtoken);

        DeleteRequest request = new DeleteRequest().setKey(key).setTableName(TABLE_NAME);

        database.getHandle().delete(request);
    }

    @Override
    public void save(String authtoken, Entry entry) throws ParseException {

        MapValue value = new MapValue()
                .put(ID, entry.getId())
                .put(AUTHTOKEN, authtoken)
                .put(JSON, entry.toJson())
                .put(TIMESTAMP, Utilities.timestampToUnix(entry.getMonth() + " " + entry.getYear()));

        PutRequest request = new PutRequest().setTableName(TABLE_NAME).setValue(value);

        database.getHandle().put(request);
    }
}
