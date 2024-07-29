package app.ezbudget.server.ezbudgetserver.model;

public class NameEdit {
    public VariableExpense old_exp;
    public VariableExpense new_exp;

    public NameEdit(VariableExpense old_exp, VariableExpense new_exp) {
        this.old_exp = old_exp;
        this.new_exp = new_exp;
    }
}
