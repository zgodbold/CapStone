public class Investments {
    private double currentSavings;
    private double currentTotalInvested;
    private double averageInvestmentReturnYearly;
    private double totalInvestmentReturn;

    public Investments(double currentSavings, double currentTotalInvested, double averageInvestmentReturnYearly, double totalInvestmentReturn) {
        this.currentSavings = currentSavings;
        this.currentTotalInvested = currentTotalInvested;
        this.averageInvestmentReturnYearly = averageInvestmentReturnYearly;
        this.totalInvestmentReturn = totalInvestmentReturn;
    }

    // Getters and Setters
    public double getCurrentSavings() { return currentSavings; }
    public void setCurrentSavings(double currentSavings) { this.currentSavings = currentSavings; }

    public double getCurrentTotalInvested() { return currentTotalInvested; }
    public void setCurrentTotalInvested(double currentTotalInvested) { this.currentTotalInvested = currentTotalInvested; }

    public double getAverageInvestmentReturnYearly() { return averageInvestmentReturnYearly; }
    public void setAverageInvestmentReturnYearly(double averageInvestmentReturnYearly) { this.averageInvestmentReturnYearly = averageInvestmentReturnYearly; }

    public double getTotalInvestmentReturn() { return totalInvestmentReturn; }
    public void setTotalInvestmentReturn(double totalInvestmentReturn) { this.totalInvestmentReturn = totalInvestmentReturn; }
}

