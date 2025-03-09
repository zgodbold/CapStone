public class MonthlyPayment {
    private double studentLoanPayment;
    private double housingPayment;
    private double carPayment;

    public MonthlyPayment(double studentLoanPayment, double housingPayment, double carPayment) {
        this.studentLoanPayment = studentLoanPayment;
        this.housingPayment = housingPayment;
        this.carPayment = carPayment;
    }

    // Getters and Setters
    public double getStudentLoanPayment() { return studentLoanPayment; }
    public void setStudentLoanPayment(double studentLoanPayment) { this.studentLoanPayment = studentLoanPayment; }

    public double getHousingPayment() { return housingPayment; }
    public void setHousingPayment(double housingPayment) { this.housingPayment = housingPayment; }

    public double getCarPayment() { return carPayment; }
    public void setCarPayment(double carPayment) { this.carPayment = carPayment; }
}

