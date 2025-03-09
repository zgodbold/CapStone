public class MonthlyResult {
    private int month;
    private double savings;
    private double invested;
    private double investmentReturn;

    public MonthlyResult(int month, double savings, double invested, double investmentReturn) {
        this.month = month;
        this.savings = savings;
        this.invested = invested;
        this.investmentReturn = investmentReturn;
    }

    // Getters
    public int getMonth() { return month; }
    public double getSavings() { return savings; }
    public double getInvested() { return invested; }
    public double getInvestmentReturn() { return investmentReturn; }

    @Override
    public String toString() {
        return String.format("Month %d: Savings = $%,.2f, Invested = $%,.2f, Investment Return = $%,.2f",
                month, savings, invested, investmentReturn);
    }
}
