package app.ezbudget.server.ezbudgetserver.model;

public class CalculatedExpense extends Expense {

    public static String PERCENT_OF = "percent";
    public static String EXACT = "exact";
    public static String NET_INCOME = "net_income";
    public static String GROSS_INCOME = "gross_income";
    public String calc_type;
    public String income_type;

    public CalculatedExpense(int id, String name, float amount, String calc_type, String income_type) {
        super(id, name, amount);
        this.calc_type = calc_type;
        this.income_type = income_type;
    }

    public String getCalcType() {
        return calc_type;
    }

    public String getIncomeType() {
        return income_type;
    }

    @Override
    public String toString() {
        return "{ id: " + this.id + ", name: " + this.name + ", amount: " + this.amount +
                ", calc_type: " + this.calc_type + ", income_type: " + this.income_type + " }";
    }
}
