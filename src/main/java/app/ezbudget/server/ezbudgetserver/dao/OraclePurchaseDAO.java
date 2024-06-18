package app.ezbudget.server.ezbudgetserver.dao;

import app.ezbudget.server.ezbudgetserver.database.Database;
import app.ezbudget.server.ezbudgetserver.model.PurchasedExpense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.ops.DeleteRequest;
import oracle.nosql.driver.ops.GetRequest;
import oracle.nosql.driver.ops.GetResult;
import oracle.nosql.driver.ops.PutRequest;
import oracle.nosql.driver.values.MapValue;

import java.util.Map;

public class OraclePurchaseDAO implements PurchaseDAO {

    private final String TABLE_NAME = "daily_purchases";
    private final String KEY = "authtoken";
    private final String JSON = "data";
    private Database<NoSQLHandle> database;
    private Gson gson;

    public OraclePurchaseDAO(Database database) {
        this.database = database;
        this.gson = new Gson();
    }

    @Override
    public Map<String, PurchasedExpense> getExpensesWithPurchases(String authtoken) {
        MapValue key = new MapValue().put(KEY, authtoken);

        GetRequest request = new GetRequest().setKey(key).setTableName(TABLE_NAME);

        GetResult result = database.getHandle().get(request);

        if(result.getValue() == null)
            throw new NullPointerException("Item does not exist");

        MapValue row = result.getValue();

        Map<String, PurchasedExpense> data = gson.fromJson(row.getString(JSON), new TypeToken<Map<String, PurchasedExpense>>() {}.getType());

        return data;
    }

    @Override
    public void save(String authtoken, Map<String, PurchasedExpense> items) {
        MapValue value = new MapValue()
                .put(KEY, authtoken)
                .put(JSON, gson.toJson(items));


        PutRequest putRequest = new PutRequest().setValue(value).setTableName(TABLE_NAME);

        database.getHandle().put(putRequest);
    }

    @Override
    public void delete(String authtoken) {
        MapValue key = new MapValue().put(KEY, authtoken);

        DeleteRequest delRequest = new DeleteRequest().setTableName(TABLE_NAME).setKey(key);

        database.getHandle().delete(delRequest);
    }
}
