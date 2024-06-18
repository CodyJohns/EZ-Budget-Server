package app.ezbudget.server.ezbudgetserver.model;

import java.util.List;

public class BudgetOverview {

    public float averageMonthlyIncome;
    public List<CalculatedExpense> calculatedExpenses;
    public List<VariableExpense> variableExpenses;

    public BudgetOverview(float avgIncome, List<CalculatedExpense> calculatedExpenses, List<VariableExpense> variableExpenses) {
        this.averageMonthlyIncome = avgIncome;
        this.calculatedExpenses = calculatedExpenses;
        this.variableExpenses = variableExpenses;
    }

    public BudgetOverview(List<CalculatedExpense> calculatedExpenses, List<VariableExpense> variableExpenses) {
        this.averageMonthlyIncome = -1F;
        this.calculatedExpenses = calculatedExpenses;
        this.variableExpenses = variableExpenses;
    }

    /**
     * Calculates and sets the average monthly income
     * @param entries
     */
    public void calculateAverageMonthlyIncome(List<Entry> entries) {
        float sum = 0;

        for(Entry entry : entries)
            sum += entry.getGross();

        if(entries.size() > 0)
            this.averageMonthlyIncome = sum / entries.size();
        else
            this.averageMonthlyIncome = sum;
    }
}
