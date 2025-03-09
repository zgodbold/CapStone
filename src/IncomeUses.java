public class IncomeUses {
    private double percentInvested;
    private double percentSaved;
    private double percentSpent;

    public IncomeUses(double percentInvested, double percentSaved, double percentSpent) {
        this.percentInvested = percentInvested;
        this.percentSaved = percentSaved;
        this.percentSpent = percentSpent;
    }

    // Getters and Setters
    public double getPercentInvested() { return percentInvested; }
    public void setPercentInvested(double percentInvested) { this.percentInvested = percentInvested; }

    public double getPercentSaved() { return percentSaved; }
    public void setPercentSaved(double percentSaved) { this.percentSaved = percentSaved; }

    public double getPercentSpent() { return percentSpent; }
    public void setPercentSpent(double percentSpent) { this.percentSpent = percentSpent; }
}

