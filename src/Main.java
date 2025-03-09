import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            File file = new File("/Users/zellgodbold/IdeaProjects/CapStone/src/FinanceSimulatorSheet.txt");
            Scanner scanner = new Scanner(file);

            // Skip the introductory text
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("Total Monthly Income Post Tax:")) {
                    break; // Stop skipping when we reach the data
                }
            }

            // Use ArrayList to store parsed data
            List<Double> dataList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Check if the line contains a colon
                if (!line.contains(":")) {
                    System.err.println("Warning: Skipping invalid line (missing colon): " + line);
                    continue;
                }

                // Extract the value part after the colon
                String value = line.split(":")[1].trim();

                // Remove "$", ",", and "%" from the value
                value = value.replace("$", "").replace(",", "").replace("%", "");

                // Parse the value and add it to the list
                try {
                    dataList.add(Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Skipping invalid value (not a number): " + value);
                }
            }

            // Debug: Print the parsed data
            System.out.println("Parsed Data: " + dataList);

            // Check if we have enough data
            if (dataList.size() < 12) {
                throw new RuntimeException("Error: The file does not contain enough data. Expected 12 values, but found " + dataList.size());
            }

            // Extract data from ArrayList
            double totalMonthlyIncome = dataList.get(0);
            double percentInvested = dataList.get(1);
            double percentSaved = dataList.get(2);
            double percentSpent = dataList.get(3);
            double currentSavings = dataList.get(4);
            double currentTotalInvested = dataList.get(5);
            double averageInvestmentReturnYearly = dataList.get(6);
            double totalInvestmentReturn = dataList.get(7);
            double studentLoanPayment = dataList.get(8);
            double housingPayment = dataList.get(9);
            double carPayment = dataList.get(10);
            int monthsToSimulate = dataList.get(11).intValue();

            // Create objects
            IncomeUses incomeUses = new IncomeUses(percentInvested, percentSaved, percentSpent);
            MonthlyPayment monthlyPayments = new MonthlyPayment(studentLoanPayment, housingPayment, carPayment);
            MonthlyIncome monthlyIncome = new MonthlyIncome(totalMonthlyIncome);
            Investments investments = new Investments(currentSavings, currentTotalInvested, averageInvestmentReturnYearly, totalInvestmentReturn);

            // Use LinkedList to store simulation results
            LinkedList<MonthlyResult> simulationResults = new LinkedList<>();

            // Start recursive simulation
            simulate(incomeUses, monthlyPayments, monthlyIncome, investments, monthsToSimulate, simulationResults, 1);

            // Print simulation results
            System.out.println("Simulation Results:");
            for (MonthlyResult result : simulationResults) {
                System.out.println(result);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found. Please ensure 'financial_data.txt' exists.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static void simulate(IncomeUses incomeUses, MonthlyPayment monthlyPayment, MonthlyIncome monthlyIncome, Investments investments, int monthsLeft, LinkedList<MonthlyResult> simulationResults, int currentMonth) {
        // Base case: stop recursion when no months are left
        if (monthsLeft <= 0) {
            return;
        }

        double monthlyInvestmentReturn = investments.getAverageInvestmentReturnYearly() / 12 / 100;

        // Simulate one month
        double income = monthlyIncome.getTotalMonthlyIncome();
        double invested = income * incomeUses.getPercentInvested() / 100;
        double saved = income * incomeUses.getPercentSaved() / 100;
        double spent = income * incomeUses.getPercentSpent() / 100;

        investments.setCurrentSavings(investments.getCurrentSavings() + saved);
        investments.setCurrentTotalInvested(investments.getCurrentTotalInvested() + invested);
        investments.setTotalInvestmentReturn(investments.getTotalInvestmentReturn() + investments.getCurrentTotalInvested() * monthlyInvestmentReturn);

        double totalExpenses = monthlyPayment.getStudentLoanPayment() + monthlyPayment.getHousingPayment() + monthlyPayment.getCarPayment() + spent;
        investments.setCurrentSavings(investments.getCurrentSavings() - totalExpenses);

        // Add monthly results to LinkedList
        simulationResults.add(new MonthlyResult(
                currentMonth,
                investments.getCurrentSavings(),
                investments.getCurrentTotalInvested(),
                investments.getTotalInvestmentReturn()
        ));

        // Recursive call for the next month
        simulate(incomeUses, monthlyPayment, monthlyIncome, investments, monthsLeft - 1, simulationResults, currentMonth + 1);
    }
}