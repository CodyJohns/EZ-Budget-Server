package app.ezbudget.server.ezbudgetserver.model;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class Entry {

    public String id;
    public String timestamp;
    public String month;
    public int year;
    public float gross;
    public float taxes;
    public float net;
    public float left;

    public List<VariableExpense> expenses;
    public List<CalculatedExpense> calculated_expenses;
    public boolean marked;

    public Entry(String id, String month, int year, float gross, float taxes, float net,
                 float left, boolean marked, List<VariableExpense> expenses, List<CalculatedExpense> calculated_expenses) {
        this.id = id;
        this.timestamp = month + " " + year;
        this.month = month;
        this.year = year;
        this.gross = gross;
        this.taxes = taxes;
        this.net = net;
        this.left = left;
        this.marked = marked;
        this.expenses = expenses;
        this.calculated_expenses = calculated_expenses;
    }

    public Entry(String id, String month, int year, float gross, float taxes, float net, float left) {
        this(id, month, year, gross, taxes, net, left, false, new ArrayList<>(), new ArrayList<>());
    }

    public void addVariableExpense(VariableExpense expense) {
        this.expenses.add(expense);
    }

    public void addCalculatedExpense(CalculatedExpense expense) {
        this.calculated_expenses.add(expense);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setId(String id) { this.id = id; }

    public String getId() { return id; }

    public String getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public float getGross() {
        return gross;
    }

    public float getTaxes() {
        return taxes;
    }

    public float getNet() {
        return net;
    }

    public float getLeft() {
        return left;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public List<VariableExpense> getVariableExpenses() {
        return expenses;
    }

    public List<CalculatedExpense> getCalculatedExpenses() {
        return calculated_expenses;
    }

    @Override
    public String toString() {
        return "{ id: " + this.id + ", month: " + this.month + ", year: " + this.year +
                ", gross: " + this.gross + ", net: " + this.net + ", taxes: " + this.taxes +
                ", left: " + this.left + ", marked: " + this.marked +
                ", variable_expenses: " + this.expenses.toString() +
                ", calculated_expenses: " + this.calculated_expenses.toString() + " }";
    }

    public BasicEntry getBasicEntry() {
        return new BasicEntry(this.id, this.month + " " + this.year);
    }
}
