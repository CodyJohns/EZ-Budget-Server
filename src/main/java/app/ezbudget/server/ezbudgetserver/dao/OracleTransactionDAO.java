package app.ezbudget.server.ezbudgetserver.dao;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.model.plaid.PlaidItem;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.DeleteRequest;
import oracle.nosql.driver.ops.GetRequest;
import oracle.nosql.driver.ops.GetResult;
import oracle.nosql.driver.ops.PrepareRequest;
import oracle.nosql.driver.ops.PrepareResult;
import oracle.nosql.driver.ops.PutRequest;
import oracle.nosql.driver.ops.QueryRequest;
import oracle.nosql.driver.ops.QueryResult;
import oracle.nosql.driver.values.MapValue;
import oracle.nosql.driver.values.StringValue;

public class OracleTransactionDAO implements TransactionDAO {

    private final String TABLE_NAME = "plaid_items";
    private final String KEY = "item_id";
    private final String JSON = "data";
    private Database<NoSQLHandle> database;
    private Gson gson;

    public OracleTransactionDAO(Database database) {
        this.database = database;
        this.gson = new Gson();
    }

    @Override
    public PlaidItem getItem(String item_id) {
        MapValue key = new MapValue().put(KEY, item_id);

        GetRequest request = new GetRequest().setKey(key).setTableName(TABLE_NAME);

        GetResult result = database.getHandle().get(request);

        if (result.getValue() == null)
            throw new NullPointerException("Item does not exist");

        MapValue row = result.getValue().asMap();

        return gson.fromJson(row.getMap().get(JSON).toJson(), PlaidItem.class);
    }

    @Override
    public List<PlaidItem> getItemsByAuthtoken(String authtoken) {
        String query = "DECLARE $iden_value STRING; " +
                "SELECT * FROM " + TABLE_NAME + " t WHERE t.data.authtoken = $iden_value";

        PrepareRequest prepReq = new PrepareRequest().setStatement(query);

        PrepareResult prepRes = database.getHandle().prepare(prepReq);

        prepRes.getPreparedStatement().setVariable("$iden_value", new StringValue(authtoken));

        QueryRequest request = new QueryRequest().setPreparedStatement(prepRes);

        QueryResult result = database.getHandle().query(request);

        List<MapValue> rows = result.getResults();

        return rows.stream().map(row -> gson.fromJson(row.getMap().get(JSON).toJson(), PlaidItem.class))
                .collect(Collectors.toList());
    }

    @Override
    public void saveItem(PlaidItem item) {
        MapValue value = new MapValue()
                .put(KEY, item.item_id)
                .putFromJson(JSON, gson.toJson(item), null);

        PutRequest request = new PutRequest().setTableName(TABLE_NAME).setValue(value);

        database.getHandle().put(request);
    }

    @Override
    public void deleteItem(PlaidItem item) {
        MapValue key = new MapValue().put(KEY, item.item_id);

        DeleteRequest delRequest = new DeleteRequest().setTableName(TABLE_NAME).setKey(key);

        database.getHandle().delete(delRequest);
    }

}
