public class IncomeUses {

    private double[] income = new double[3];

    public double[] setIncomeInvested(double amount) {
        income[0] = amount;
        return income;
    }

    public double getIncomeInvested(double amount) {
        return income[0];
    }

    public double[] setIncomeSaved(double amount) {
        income[1] = amount;
        return income;
    }

    public double getIncomeSaved(double amount) {
        return income[1];
    }

    public double[] setIncomeSpent(double amount) {
        income[2] = amount;
        return income;
    }

    public double getIncomeSpent(double amount) {
        return income[2];
    }

}
