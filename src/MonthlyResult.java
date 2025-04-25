public class MonthlyResult {
    private final int month;
    private final double savings;
    private final double invested;
    private final double investmentReturn;
    private final double spent;
    private final double investedAmount;
    private final double income;

    public MonthlyResult(int month, double savings, double invested, double investmentReturn,
                         double spent, double investedAmount, double income) {
        this.month = month;
        this.savings = savings;
        this.invested = invested;
        this.investmentReturn = investmentReturn;
        this.spent = spent;
        this.investedAmount = investedAmount;
        this.income = income;
    }

    // Getters
    public int getMonth() { return month; }
    public double getSavings() { return savings; }
    public double getInvested() { return invested; }
    public double getInvestmentReturn() { return investmentReturn; }
    public double getSpent() { return spent; }
    public double getInvestedAmount() { return investedAmount; }
    public double getIncome() { return income; }

    @Override
    public String toString() {
        return String.format("Month %d: Income = $%,.2f, Spent = $%,.2f, New Investments = $%,.2f, Savings = $%,.2f, Total Invested = $%,.2f, Return = $%,.2f",
                month, income, spent, investedAmount, savings, invested, investmentReturn);
    }
}
